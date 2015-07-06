package de.schierla.jbeagle;

public class RenderOptions {
	private int cropBottom = 0, cropTop = 0, cropLeft = 0, cropRight = 0;
	private float scaleX = 1f, scaleY = 1f;
	private float contrast, brightness;
	private float gamma;
	private boolean antiAlias, favorQuality;

	// public RenderOptions(int cropX, int cropY, float scaleX, float scaleY) {
	// super();
	// this.cropX = cropX;
	// this.cropY = cropY;
	// this.scaleX = scaleX;
	// this.scaleY = scaleY;
	// }

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getContrast() {
		return contrast;
	}

	public void setContrast(float contrast) {
		this.contrast = contrast;
	}

	public float getGamma() {
		return gamma;
	}

	public void setGamma(float gamma) {
		this.gamma = gamma;
	}

	public int getCropRight() {
		return cropRight;
	}

	public void setCropRight(int cropRight) {
		this.cropRight = cropRight;
	}

	public int getCropLeft() {
		return cropLeft;
	}

	public void setCropLeft(int cropLeft) {
		this.cropLeft = cropLeft;
	}

	public int getCropTop() {
		return cropTop;
	}

	public void setCropTop(int cropTop) {
		this.cropTop = cropTop;
	}

	public int getCropBottom() {
		return cropBottom;
	}

	public void setCropBottom(int cropBottom) {
		this.cropBottom = cropBottom;
	}

	public boolean isAntiAlias() {
		return antiAlias;
	}

	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	public boolean isFavorQuality() {
		return favorQuality;
	}

	public void setFavorQuality(boolean favorQuality) {
		this.favorQuality = favorQuality;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
}