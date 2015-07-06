package de.schierla.jbeagle.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

import de.schierla.jbeagle.BeagleRenderer;
import de.schierla.jbeagle.RenderOptions;

public class PreviewContainer {

	private boolean antialiasing;
	private boolean favorQuality;

	private BufferedImage previewedImage;

	private static final PreviewContainer singleton = new PreviewContainer();
	private static JComponent previewPane;

	private JSpinner cropTop, cropBottom, cropLeft, cropRight, scaleX, scaleY,
			pageNumber, brightness, contrast;
	private JCheckBox doAntialias, doQuality;

	private File file;
	private PdfDecoder decoder;

	JButton previewBtn;

	private final int previewWidth = 600;
	private final int previewHeight = 800;
	private final int spinnerWidth = 50;
	private final int spinnerHeight = 20;

	private PreviewContainer() {
		previewPane = new JComponent() {
			private static final long serialVersionUID = 1L;

			@Override
			public Dimension getMinimumSize() {
				return new Dimension(100, 100);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(previewWidth, previewHeight);
			}

			@Override
			public void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				if (singleton.previewedImage != null) {
					g2d.drawImage(singleton.previewedImage, 0, 0, null);
				}

				g2d.setColor(Color.black);
				g2d.drawRect(-1, -1, previewWidth + 2, previewHeight + 2);

				super.paintComponent(g2d);

			}
		};
		previewPane.setLayout(null);

		{
			cropTop = createOverlayedSpinner();
			previewPane.add(cropTop);
			cropTop.setLocation((previewWidth - spinnerWidth) / 2, 0);
			cropTop.setToolTipText("How many pixels to crop from the top side");

			cropLeft = createOverlayedSpinner();
			previewPane.add(cropLeft);
			cropLeft.setLocation(0, (previewHeight - spinnerHeight) / 2);
			cropLeft.setToolTipText("How many pixels to crop from the left side");

			cropBottom = createOverlayedSpinner();
			previewPane.add(cropBottom);
			cropBottom.setLocation((previewWidth - spinnerWidth) / 2,
					previewHeight - spinnerHeight);
			cropBottom
					.setToolTipText("How many pixels to crop from the bottom side");

			cropRight = createOverlayedSpinner();
			previewPane.add(cropRight);
			cropRight.setLocation(previewWidth - spinnerWidth,
					(previewHeight - spinnerHeight) / 2);
			cropRight
					.setToolTipText("How many pixels to crop from the right side");

			scaleX = createOverlayedSpinner();
			scaleX.setValue(100);
			previewPane.add(scaleX);
			scaleX.setLocation(
					(previewWidth - spinnerWidth) / 2 - spinnerWidth,
					(previewHeight - spinnerHeight) / 2);
			scaleX.setToolTipText("X scale in percent");

			scaleY = createOverlayedSpinner();
			scaleY.setValue(100);
			previewPane.add(scaleY);
			scaleY.setLocation((previewWidth - spinnerWidth) / 2,
					(previewHeight - spinnerHeight) / 2);
			scaleY.setToolTipText("Y scale in percent");
		}

		JPanel detail = new JPanel();
		{

			JPanel jp3 = new JPanel();
			previewBtn = new JButton("Preview");
			// previewBtn.setEnabled(false);

			pageNumber = new JSpinner();
			pageNumber.setValue(1);
			pageNumber.setToolTipText("Page number");
			setMinSpinnerWidth(pageNumber, 3);

			brightness = new JSpinner();
			brightness.setValue(0);
			brightness.setToolTipText("Brightness");
			setMinSpinnerWidth(brightness, 3);

			contrast = new JSpinner();
			contrast.setValue(100);
			contrast.setToolTipText("Contrast");
			setMinSpinnerWidth(contrast, 3);

			doAntialias = new JCheckBox("Antialias");
			doAntialias.setSelected(true);
			doQuality = new JCheckBox("Quality");
			doQuality.setSelected(true);

			jp3.add(pageNumber, createGbc(0, 6, 5));
			jp3.add(previewBtn, createGbc(GridBagConstraints.RELATIVE, 6, 5));
			jp3.add(doAntialias, createGbc(GridBagConstraints.RELATIVE, 6, 5));
			jp3.add(doQuality, createGbc(GridBagConstraints.RELATIVE, 6, 5));
			jp3.add(brightness, createGbc(GridBagConstraints.RELATIVE, 6, 5));
			jp3.add(contrast, createGbc(GridBagConstraints.RELATIVE, 6, 5));

			detail.add(jp3, createGbc(0, 6, 0));

			ActionListener previewAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					doPreview();
				}
			};

			ChangeListener previewAction2 = new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					doPreview();
				}
			};

			previewBtn.addActionListener(previewAction);
			doAntialias.addActionListener(previewAction);
			doQuality.addActionListener(previewAction);

			pageNumber.addChangeListener(previewAction2);
			brightness.addChangeListener(previewAction2);
			contrast.addChangeListener(previewAction2);

			cropTop.addChangeListener(previewAction2);
			cropBottom.addChangeListener(previewAction2);
			cropLeft.addChangeListener(previewAction2);
			cropRight.addChangeListener(previewAction2);
			scaleX.addChangeListener(previewAction2);
			scaleY.addChangeListener(previewAction2);
		}

		JFrame frame = new JFrame();
		frame.add(previewPane, BorderLayout.CENTER);
		frame.add(detail, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}

	public static PreviewContainer getInstance() {
		return PreviewContainer.singleton;
	}

	public static RenderOptions getRenderOptions() {
		RenderOptions result = new RenderOptions();
		result.setCropTop((Integer) getInstance().cropTop.getValue());
		result.setCropBottom((Integer) getInstance().cropBottom.getValue());
		result.setCropLeft((Integer) getInstance().cropLeft.getValue());
		result.setCropRight((Integer) getInstance().cropRight.getValue());
		result.setScaleX((float) ((Integer) getInstance().scaleX.getValue() / 100f));
		result.setScaleY((float) ((Integer) getInstance().scaleY.getValue() / 100f));
		
		result.setAntiAlias(getInstance().doAntialias.isSelected());
		result.setFavorQuality(getInstance().doQuality.isSelected());
		
		result.setBrightness((Integer) (getInstance().brightness.getValue()));
		result.setContrast((float) ((Integer) getInstance().contrast.getValue() / 100f));
		return result;
	}

	public static void setPreviewedImage(BufferedImage image) {
		singleton.previewedImage = image;
		getPreviewPane().repaint();
	}

	public void setSelectedFile(final File file) {
		singleton.file = file;
		try {
			if (decoder != null && decoder.isOpen()) {
				decoder.closePdfFile();
			}

			// XXX: reset decoder completely, there are some strange bugs
			// otherwise
			decoder = new PdfDecoder(true);
			decoder.openPdfFile(file.getAbsolutePath());

			if (decoder.isOpen()) {

				((SpinnerNumberModel) pageNumber.getModel()).setMaximum(decoder
						.getPageCount());
				((SpinnerNumberModel) pageNumber.getModel()).setMinimum(1);
				pageNumber.setValue(1);
				// previewBtn.doClick(); // called by page spinner anyway
			}
		} catch (PdfException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected boolean isAntialiasing() {
		return antialiasing;
	}

	protected void setAntialiasing(boolean antialiasing) {
		singleton.antialiasing = antialiasing;
		getPreviewPane().repaint();
	}

	protected boolean isFavorQuality() {
		return favorQuality;
	}

	protected void setFavorQuality(boolean favorQuality) {
		singleton.favorQuality = favorQuality;
		getPreviewPane().repaint();
	}

	private static JComponent getPreviewPane() {
		return previewPane;
	}

	private GridBagConstraints createGbc(int gridX, int gridY, int bottomInset) {
		GridBagConstraints result = new GridBagConstraints();
		result.fill = GridBagConstraints.HORIZONTAL;
		result.insets = new Insets(0, 0, bottomInset, 5);
		result.gridx = gridX;
		result.gridy = gridY;
		return result;
	}

	private void setMinSpinnerWidth(JSpinner spinner, int columns) {
		NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
		// DecimalFormat format = editor.getFormat();
		JFormattedTextField textField = ((JSpinner.DefaultEditor) editor)
				.getTextField();
		// format.setMinimumFractionDigits(digits);
		textField.setColumns(columns);
	}

	private JSpinner createOverlayedSpinner() {
		final JSpinner spinner = new JSpinner();
		setMinSpinnerWidth(spinner, 4);
		spinner.setValue(0);
		spinner.setSize(spinnerWidth, spinnerHeight);
		makeTransparent(spinner);
		return spinner;
	}

	private static void makeTransparent(Component c) {
		if (c instanceof Container) {
			for (Component child : ((Container) c).getComponents())
				makeTransparent(child);
		}
		if (c instanceof JComponent) {
			JComponent c2 = (JComponent) c;
			// c2.setOpaque(false);
			c2.setBackground(new Color(1, 1, 1, 0.2f));
		}
	}

	private void doPreview() {
		int pageNo = (Integer) pageNumber.getValue() - 1;
		if (decoder != null && decoder.isOpen()) {
			setAntialiasing(doAntialias.isSelected());
			setFavorQuality(doQuality.isSelected());

			// I have no idea why these don't work
			decoder.setSize(new Dimension(previewWidth * 2,
					previewHeight * 2));
			decoder.setPreferredSize(new Dimension(previewWidth * 2,
					previewHeight * 2));
			decoder.useHiResScreenDisplay(true);
			
			BeagleRenderer renderer = new BeagleRenderer(decoder, "", "",
					getRenderOptions());

			BufferedImage image = renderer.render(pageNo, false);
			PreviewContainer.setPreviewedImage(image);
		}
	}

}
