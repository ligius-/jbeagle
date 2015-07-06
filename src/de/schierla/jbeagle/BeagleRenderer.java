/*
 * jBeagle - application for managing the txtr beagle
 * Copyright 2013 Andreas Schierl
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.schierla.jbeagle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.List;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.objects.PdfPageData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BeagleRenderer {

	private PdfDecoder decoder;
	private String author;
	private String title;
	private int pages;
	private List<Integer> bookmarks;
	private RenderOptions renderOptions;

	public BeagleRenderer(PdfDecoder decoder, String author, String title,
			RenderOptions renderOptions) {
		this.decoder = decoder;
		this.author = author;
		this.title = title;
		this.pages = decoder.getPageCount();
		this.bookmarks = extractBookmarks(decoder);
		this.renderOptions = renderOptions;
	}

	public int getPageCount() {
		return this.pages;
	}

	/**
	 * Renders the specified page
	 * 
	 * @param nr
	 *            number of page to render (0-based)
	 * @param titelPage
	 *            draw the given page as title page
	 * @return Image of the page
	 */
	public BufferedImage render(int nr, boolean titlePage) {
		BufferedImage im = new BufferedImage((int) (600f * 1),
				(int) (800f * 1), BufferedImage.TYPE_4BYTE_ABGR);
		try {
			decoder.setPageParameters(1, -1);
			BufferedImage page = decoder.getPageAsImage(nr + 1);
			float scaleX = renderOptions.getScaleX() * 600f / page.getWidth();
			float scaleY = renderOptions.getScaleY() * 800f / page.getHeight();
			decoder.setPageParameters(Math.min(scaleX, scaleY), -1);
			// decoder.setPageData(new PdfPageData());
			// decoder.getPageData();
			decoder.setAllowDifferentPrintPageSizes(true);
			// {
			// PdfPageData pageData = new PdfPageData();
			// pageData.setCropBox(new float[] { renderOptions.getCropX(),
			// renderOptions.getCropY(), 600, 800 });
			// decoder.setPageData(pageData);
			// }

			page = decoder.getPageAsImage(nr + 1);
			if (titlePage) {
				drawTitlePage(author, title, page, im.getGraphics(),
						renderOptions);
			} else {
				drawBookPage((pages - 1), nr, page, im.getGraphics(),
						bookmarks, renderOptions);
			}
		} catch (PdfException e) {
			e.printStackTrace();
		}
		return im;
	}

	private List<Integer> extractBookmarks(PdfDecoder decoder) {
		Document outline = decoder.getOutlineAsXML();
		final List<Integer> bookmarks = new ArrayList<Integer>();
		if (outline != null) {
			NodeList titles = outline.getElementsByTagName("title");
			for (int i = 0; i < titles.getLength(); i++) {
				Node pageAttribute = titles.item(i).getAttributes()
						.getNamedItem("page");
				if (pageAttribute != null) {
					bookmarks.add(Integer.parseInt(pageAttribute
							.getTextContent()));
				}
			}
		}
		return bookmarks;
	}

	private static void drawTitlePage(final String author, final String title,
			BufferedImage page, Graphics output, RenderOptions renderOptions) {
		output.setColor(Color.white);
		output.fillRect(0, 0, 600, 800);
		output.setColor(Color.black);
		output.drawImage(page, 150, 200, 300, 400, null);
		output.setFont(new Font("SansSerif", Font.BOLD, 30));
		Rectangle2D bounds = output.getFontMetrics().getStringBounds(title,
				output);
		output.drawString(title, (int) (600 - bounds.getWidth()) / 2,
				800 - 5 - (int) bounds.getMaxY());
		bounds = output.getFontMetrics().getStringBounds(author, output);
		output.drawString(author, (int) (600 - bounds.getWidth()) / 2,
				5 - (int) bounds.getMinY());
	}

	private static void drawBookPage(int pages, int i, BufferedImage page,
			Graphics output, List<Integer> bookmarks,
			RenderOptions renderOptions) {
		output.setColor(Color.white);
		output.fillRect(0, 0, 600, 800);
		float width = page.getWidth(), height = page.getHeight();
		float scaleX = 600 / width, scaleY = 800 / height;
		float scale = Math.min(scaleX, scaleY);
		width = width * scale * renderOptions.getScaleX()
				+ renderOptions.getCropRight();
		height = height * scale * renderOptions.getScaleY()
				+ renderOptions.getCropBottom();
		float x = (600 - width + renderOptions.getCropRight()) / 2
				- renderOptions.getCropLeft();
		float y = (800 - height + renderOptions.getCropBottom()) / 2
				- renderOptions.getCropTop();

		
		Graphics2D g2d = (Graphics2D) output;

		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_RENDERING,
				renderOptions.isFavorQuality() ? RenderingHints.VALUE_RENDER_QUALITY
						: RenderingHints.VALUE_RENDER_SPEED);

		hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);

		hints.put(RenderingHints.KEY_ANTIALIASING,
				renderOptions.isAntiAlias() ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, renderOptions
				.isAntiAlias() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
				: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		g2d.setRenderingHints(hints);

		RescaleOp rescale = new RescaleOp(renderOptions.getContrast(), renderOptions.getBrightness(), hints);
		rescale.filter(page, page);
		
		output.drawImage(page, (int) x, (int) y, (int) width, (int) height,
				null);

		// avoid division by zero
		if (pages > 0) {
			output.setColor(Color.GRAY);
			output.fillRect(0, 795, (600 * i / pages), 5);
		}

		output.setColor(Color.BLACK);
		for (Integer nr : bookmarks) {
			output.drawLine(600 * (nr - 1) / pages, 797,
					600 * (nr - 1) / pages, 799);
		}
	}
}
