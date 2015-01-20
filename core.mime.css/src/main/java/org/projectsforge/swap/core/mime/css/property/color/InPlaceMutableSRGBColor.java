package org.projectsforge.swap.core.mime.css.property.color;

public class InPlaceMutableSRGBColor extends AbstractMutableSRGBColor {

  /** The r. */
  private int r;

  /** The g. */
  private int g;

  /** The b. */
  private int b;

  /**
   * Instantiates a new mutable srgb color.
   * 
   * @param r the r
   * @param g the g
   * @param b the b
   */
  public InPlaceMutableSRGBColor(final double r, final double g, final double b) {
    set(r, g, b);
  }

  /**
   * Instantiates a new mutable srgb color.
   * 
   * @param rgb the rgb
   */
  public InPlaceMutableSRGBColor(final double[] rgb) {
    set(rgb);
  }

  /**
   * Instantiates a new mutable srgb color.
   * 
   * @param r the r
   * @param g the g
   * @param b the b
   */
  public InPlaceMutableSRGBColor(final int r, final int g, final int b) {
    set(r, g, b);
  }

  /**
   * Instantiates a new mutable srgb color.
   * 
   * @param rgb the rgb
   */
  public InPlaceMutableSRGBColor(final int[] rgb) {
    set(rgb);
  }

  /**
   * Instantiates a new mutable srgb color.
   * 
   * @param color the color
   */
  public InPlaceMutableSRGBColor(final SRGBColor color) {
    setR(color.getR());
    setG(color.getG());
    setB(color.getB());
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.SRGBColor#getB()
   */
  @Override
  public int getB() {
    return b;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.SRGBColor#getG()
   */
  @Override
  public int getG() {
    return g;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.SRGBColor#getR()
   */
  @Override
  public int getR() {
    return r;
  }

  /**
   * Sets the b.
   * 
   * @param b the new b
   */
  @Override
  public void setB(final int b) {
    this.b = b;
  }

  /**
   * Sets the g.
   * 
   * @param g the new g
   */
  @Override
  public void setG(final int g) {
    this.g = g;
  }

  /**
   * Sets the r.
   * 
   * @param r the new r
   */
  @Override
  public void setR(final int r) {
    this.r = r;
  }

}
