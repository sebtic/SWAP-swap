/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id: SRGBColor.java 110 2011-12-14 13:07:42Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.property.color;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The class representing a sRGB color.
 * 
 * @author Sébastien Aupetit
 */
// http://en.wikipedia.org/wiki/SRGB_color_space
public abstract class SRGBColor {

  /** The Constant aliceblue. */
  public static final ImmutableSRGBColor aliceblue = new ImmutableSRGBColor(240, 248, 255);

  /** The Constant antiquewhite. */
  public static final ImmutableSRGBColor antiquewhite = new ImmutableSRGBColor(250, 235, 215);

  /** The Constant aqua. */
  public static final ImmutableSRGBColor aqua = new ImmutableSRGBColor(0, 255, 255);

  /** The Constant aquamarine. */
  public static final ImmutableSRGBColor aquamarine = new ImmutableSRGBColor(127, 255, 212);

  /** The Constant azure. */
  public static final ImmutableSRGBColor azure = new ImmutableSRGBColor(240, 255, 255);

  /** The Constant beige. */
  public static final ImmutableSRGBColor beige = new ImmutableSRGBColor(245, 245, 220);

  /** The Constant bisque. */
  public static final ImmutableSRGBColor bisque = new ImmutableSRGBColor(255, 228, 196);

  /** The Constant black. */
  public static final ImmutableSRGBColor black = new ImmutableSRGBColor(0, 0, 0);

  /** The Constant blanchedalmond. */
  public static final ImmutableSRGBColor blanchedalmond = new ImmutableSRGBColor(255, 235, 205);

  /** The Constant blue. */
  public static final ImmutableSRGBColor blue = new ImmutableSRGBColor(0, 0, 255);

  /** The Constant blueviolet. */
  public static final ImmutableSRGBColor blueviolet = new ImmutableSRGBColor(138, 43, 226);

  /** The Constant brown. */
  public static final ImmutableSRGBColor brown = new ImmutableSRGBColor(165, 42, 42);

  /** The Constant burlywood. */
  public static final ImmutableSRGBColor burlywood = new ImmutableSRGBColor(222, 184, 135);

  /** The Constant cadetblue. */
  public static final ImmutableSRGBColor cadetblue = new ImmutableSRGBColor(95, 158, 160);

  /** The Constant chartreuse. */
  public static final ImmutableSRGBColor chartreuse = new ImmutableSRGBColor(127, 255, 0);

  /** The Constant chocolate. */
  public static final ImmutableSRGBColor chocolate = new ImmutableSRGBColor(210, 105, 30);

  /** The Constant coral. */
  public static final ImmutableSRGBColor coral = new ImmutableSRGBColor(255, 127, 80);

  /** The Constant cornflowerblue. */
  public static final ImmutableSRGBColor cornflowerblue = new ImmutableSRGBColor(100, 149, 237);

  /** The Constant cornsilk. */
  public static final ImmutableSRGBColor cornsilk = new ImmutableSRGBColor(255, 248, 220);

  /** The Constant crimson. */
  public static final ImmutableSRGBColor crimson = new ImmutableSRGBColor(220, 20, 60);

  /** The Constant cyan. */
  public static final ImmutableSRGBColor cyan = new ImmutableSRGBColor(0, 255, 255);

  /** The Constant darkblue. */
  public static final ImmutableSRGBColor darkblue = new ImmutableSRGBColor(0, 0, 139);

  /** The Constant darkcyan. */
  public static final ImmutableSRGBColor darkcyan = new ImmutableSRGBColor(0, 139, 139);

  /** The Constant darkgoldenrod. */
  public static final ImmutableSRGBColor darkgoldenrod = new ImmutableSRGBColor(184, 134, 11);

  /** The Constant darkgray. */
  public static final ImmutableSRGBColor darkgray = new ImmutableSRGBColor(169, 169, 169);

  /** The Constant darkgreen. */
  public static final ImmutableSRGBColor darkgreen = new ImmutableSRGBColor(0, 100, 0);

  /** The Constant darkgrey. */
  public static final ImmutableSRGBColor darkgrey = new ImmutableSRGBColor(169, 169, 169);

  /** The Constant darkkhaki. */
  public static final ImmutableSRGBColor darkkhaki = new ImmutableSRGBColor(189, 183, 107);

  /** The Constant darkmagenta. */
  public static final ImmutableSRGBColor darkmagenta = new ImmutableSRGBColor(139, 0, 139);

  /** The Constant darkolivegreen. */
  public static final ImmutableSRGBColor darkolivegreen = new ImmutableSRGBColor(85, 107, 47);

  /** The Constant darkorange. */
  public static final ImmutableSRGBColor darkorange = new ImmutableSRGBColor(255, 140, 0);

  /** The Constant darkorchid. */
  public static final ImmutableSRGBColor darkorchid = new ImmutableSRGBColor(153, 50, 204);

  /** The Constant darkred. */
  public static final ImmutableSRGBColor darkred = new ImmutableSRGBColor(139, 0, 0);

  /** The Constant darksalmon. */
  public static final ImmutableSRGBColor darksalmon = new ImmutableSRGBColor(233, 150, 122);

  /** The Constant darkseagreen. */
  public static final ImmutableSRGBColor darkseagreen = new ImmutableSRGBColor(143, 188, 143);

  /** The Constant darkslateblue. */
  public static final ImmutableSRGBColor darkslateblue = new ImmutableSRGBColor(72, 61, 139);

  /** The Constant darkslategray. */
  public static final ImmutableSRGBColor darkslategray = new ImmutableSRGBColor(47, 79, 79);

  /** The Constant darkslategrey. */
  public static final ImmutableSRGBColor darkslategrey = new ImmutableSRGBColor(47, 79, 79);

  /** The Constant darkturquoise. */
  public static final ImmutableSRGBColor darkturquoise = new ImmutableSRGBColor(0, 206, 209);

  /** The Constant darkviolet. */
  public static final ImmutableSRGBColor darkviolet = new ImmutableSRGBColor(148, 0, 211);

  /** The Constant deeppink. */
  public static final ImmutableSRGBColor deeppink = new ImmutableSRGBColor(255, 20, 147);

  /** The Constant deepskyblue. */
  public static final ImmutableSRGBColor deepskyblue = new ImmutableSRGBColor(0, 191, 255);

  /** The Constant dimgray. */
  public static final ImmutableSRGBColor dimgray = new ImmutableSRGBColor(105, 105, 105);

  /** The Constant dimgrey. */
  public static final ImmutableSRGBColor dimgrey = new ImmutableSRGBColor(105, 105, 105);

  /** The Constant dodgerblue. */
  public static final ImmutableSRGBColor dodgerblue = new ImmutableSRGBColor(30, 144, 255);

  /** The Constant firebrick. */
  public static final ImmutableSRGBColor firebrick = new ImmutableSRGBColor(178, 34, 34);

  /** The Constant floralwhite. */
  public static final ImmutableSRGBColor floralwhite = new ImmutableSRGBColor(255, 250, 240);

  /** The Constant forestgreen. */
  public static final ImmutableSRGBColor forestgreen = new ImmutableSRGBColor(34, 139, 34);

  /** The Constant fuchsia. */
  public static final ImmutableSRGBColor fuchsia = new ImmutableSRGBColor(255, 0, 255);

  /** The Constant gainsboro. */
  public static final ImmutableSRGBColor gainsboro = new ImmutableSRGBColor(220, 220, 220);

  /** The Constant ghostwhite. */
  public static final ImmutableSRGBColor ghostwhite = new ImmutableSRGBColor(248, 248, 255);

  /** The Constant gold. */
  public static final ImmutableSRGBColor gold = new ImmutableSRGBColor(255, 215, 0);

  /** The Constant goldenrod. */
  public static final ImmutableSRGBColor goldenrod = new ImmutableSRGBColor(218, 165, 32);

  /** The Constant gray. */
  public static final ImmutableSRGBColor gray = new ImmutableSRGBColor(128, 128, 128);

  /** The Constant green. */
  public static final ImmutableSRGBColor green = new ImmutableSRGBColor(0, 128, 0);

  /** The Constant greenyellow. */
  public static final ImmutableSRGBColor greenyellow = new ImmutableSRGBColor(173, 255, 47);

  /** The Constant grey. */
  public static final ImmutableSRGBColor grey = new ImmutableSRGBColor(128, 128, 128);

  /** The Constant honeydew. */
  public static final ImmutableSRGBColor honeydew = new ImmutableSRGBColor(240, 255, 240);

  /** The Constant hotpink. */
  public static final ImmutableSRGBColor hotpink = new ImmutableSRGBColor(255, 105, 180);

  /** The Constant indianred. */
  public static final ImmutableSRGBColor indianred = new ImmutableSRGBColor(205, 92, 92);

  /** The Constant indigo. */
  public static final ImmutableSRGBColor indigo = new ImmutableSRGBColor(75, 0, 130);

  /** The Constant ivory. */
  public static final ImmutableSRGBColor ivory = new ImmutableSRGBColor(255, 255, 240);

  /** The Constant khaki. */
  public static final ImmutableSRGBColor khaki = new ImmutableSRGBColor(240, 230, 140);

  /** The Constant lavender. */
  public static final ImmutableSRGBColor lavender = new ImmutableSRGBColor(230, 230, 250);

  /** The Constant lavenderblush. */
  public static final ImmutableSRGBColor lavenderblush = new ImmutableSRGBColor(255, 240, 245);

  /** The Constant lawngreen. */
  public static final ImmutableSRGBColor lawngreen = new ImmutableSRGBColor(124, 252, 0);

  /** The Constant lemonchiffon. */
  public static final ImmutableSRGBColor lemonchiffon = new ImmutableSRGBColor(255, 250, 205);

  /** The Constant lightblue. */
  public static final ImmutableSRGBColor lightblue = new ImmutableSRGBColor(173, 216, 230);

  /** The Constant lightcoral. */
  public static final ImmutableSRGBColor lightcoral = new ImmutableSRGBColor(240, 128, 128);

  /** The Constant lightcyan. */
  public static final ImmutableSRGBColor lightcyan = new ImmutableSRGBColor(224, 255, 255);

  /** The Constant lightgoldenrodyellow. */
  public static final ImmutableSRGBColor lightgoldenrodyellow = new ImmutableSRGBColor(250, 250, 210);

  /** The Constant lightgray. */
  public static final ImmutableSRGBColor lightgray = new ImmutableSRGBColor(211, 211, 211);

  /** The Constant lightgreen. */
  public static final ImmutableSRGBColor lightgreen = new ImmutableSRGBColor(144, 238, 144);

  /** The Constant lightgrey. */
  public static final ImmutableSRGBColor lightgrey = new ImmutableSRGBColor(211, 211, 211);

  /** The Constant lightpink. */
  public static final ImmutableSRGBColor lightpink = new ImmutableSRGBColor(255, 182, 193);

  /** The Constant lightsalmon. */
  public static final ImmutableSRGBColor lightsalmon = new ImmutableSRGBColor(255, 160, 122);

  /** The Constant lightseagreen. */
  public static final ImmutableSRGBColor lightseagreen = new ImmutableSRGBColor(32, 178, 170);

  /** The Constant lightskyblue. */
  public static final ImmutableSRGBColor lightskyblue = new ImmutableSRGBColor(135, 206, 250);

  /** The Constant lightslategray. */
  public static final ImmutableSRGBColor lightslategray = new ImmutableSRGBColor(119, 136, 153);

  /** The Constant lightslategrey. */
  public static final ImmutableSRGBColor lightslategrey = new ImmutableSRGBColor(119, 136, 153);

  /** The Constant lightsteelblue. */
  public static final ImmutableSRGBColor lightsteelblue = new ImmutableSRGBColor(176, 196, 222);

  /** The Constant lightyellow. */
  public static final ImmutableSRGBColor lightyellow = new ImmutableSRGBColor(255, 255, 224);

  /** The Constant lime. */
  public static final ImmutableSRGBColor lime = new ImmutableSRGBColor(0, 255, 0);

  /** The Constant limegreen. */
  public static final ImmutableSRGBColor limegreen = new ImmutableSRGBColor(50, 205, 50);

  /** The Constant linen. */
  public static final ImmutableSRGBColor linen = new ImmutableSRGBColor(250, 240, 230);

  /** The Constant magenta. */
  public static final ImmutableSRGBColor magenta = new ImmutableSRGBColor(255, 0, 255);

  /** The Constant maroon. */
  public static final ImmutableSRGBColor maroon = new ImmutableSRGBColor(128, 0, 0);

  /** The Constant mediumaquamarine. */
  public static final ImmutableSRGBColor mediumaquamarine = new ImmutableSRGBColor(102, 205, 170);

  /** The Constant mediumblue. */
  public static final ImmutableSRGBColor mediumblue = new ImmutableSRGBColor(0, 0, 205);

  /** The Constant mediumorchid. */
  public static final ImmutableSRGBColor mediumorchid = new ImmutableSRGBColor(186, 85, 211);

  /** The Constant mediumpurple. */
  public static final ImmutableSRGBColor mediumpurple = new ImmutableSRGBColor(147, 112, 219);

  /** The Constant mediumseagreen. */
  public static final ImmutableSRGBColor mediumseagreen = new ImmutableSRGBColor(60, 179, 113);

  /** The Constant mediumslateblue. */
  public static final ImmutableSRGBColor mediumslateblue = new ImmutableSRGBColor(123, 104, 238);

  /** The Constant mediumspringgreen. */
  public static final ImmutableSRGBColor mediumspringgreen = new ImmutableSRGBColor(0, 250, 154);

  /** The Constant mediumturquoise. */
  public static final ImmutableSRGBColor mediumturquoise = new ImmutableSRGBColor(72, 209, 204);

  /** The Constant mediumvioletred. */
  public static final ImmutableSRGBColor mediumvioletred = new ImmutableSRGBColor(199, 21, 133);

  /** The Constant midnightblue. */
  public static final ImmutableSRGBColor midnightblue = new ImmutableSRGBColor(25, 25, 112);

  /** The Constant mintcream. */
  public static final ImmutableSRGBColor mintcream = new ImmutableSRGBColor(245, 255, 250);

  /** The Constant mistyrose. */
  public static final ImmutableSRGBColor mistyrose = new ImmutableSRGBColor(255, 228, 225);

  /** The Constant moccasin. */
  public static final ImmutableSRGBColor moccasin = new ImmutableSRGBColor(255, 228, 181);

  /** The Constant navajowhite. */
  public static final ImmutableSRGBColor navajowhite = new ImmutableSRGBColor(255, 222, 173);

  /** The Constant navy. */
  public static final ImmutableSRGBColor navy = new ImmutableSRGBColor(0, 0, 128);

  /** The Constant oldlace. */
  public static final ImmutableSRGBColor oldlace = new ImmutableSRGBColor(253, 245, 230);

  /** The Constant olive. */
  public static final ImmutableSRGBColor olive = new ImmutableSRGBColor(128, 128, 0);

  /** The Constant olivedrab. */
  public static final ImmutableSRGBColor olivedrab = new ImmutableSRGBColor(107, 142, 35);

  /** The Constant orange. */
  public static final ImmutableSRGBColor orange = new ImmutableSRGBColor(255, 165, 0);

  /** The Constant orangered. */
  public static final ImmutableSRGBColor orangered = new ImmutableSRGBColor(255, 69, 0);

  /** The Constant orchid. */
  public static final ImmutableSRGBColor orchid = new ImmutableSRGBColor(218, 112, 214);

  /** The Constant palegoldenrod. */
  public static final ImmutableSRGBColor palegoldenrod = new ImmutableSRGBColor(238, 232, 170);

  /** The Constant palegreen. */
  public static final ImmutableSRGBColor palegreen = new ImmutableSRGBColor(152, 251, 152);

  /** The Constant paleturquoise. */
  public static final ImmutableSRGBColor paleturquoise = new ImmutableSRGBColor(175, 238, 238);

  /** The Constant palevioletred. */
  public static final ImmutableSRGBColor palevioletred = new ImmutableSRGBColor(219, 112, 147);

  /** The Constant papayawhip. */
  public static final ImmutableSRGBColor papayawhip = new ImmutableSRGBColor(255, 239, 213);

  /** The Constant peachpuff. */
  public static final ImmutableSRGBColor peachpuff = new ImmutableSRGBColor(255, 218, 185);

  /** The Constant peru. */
  public static final ImmutableSRGBColor peru = new ImmutableSRGBColor(205, 133, 63);

  /** The Constant pink. */
  public static final ImmutableSRGBColor pink = new ImmutableSRGBColor(255, 192, 203);

  /** The Constant plum. */
  public static final ImmutableSRGBColor plum = new ImmutableSRGBColor(221, 160, 221);

  /** The Constant powderblue. */
  public static final ImmutableSRGBColor powderblue = new ImmutableSRGBColor(176, 224, 230);

  /** The Constant purple. */
  public static final ImmutableSRGBColor purple = new ImmutableSRGBColor(128, 0, 128);

  /** The Constant red. */
  public static final ImmutableSRGBColor red = new ImmutableSRGBColor(255, 0, 0);

  /** The Constant rosybrown. */
  public static final ImmutableSRGBColor rosybrown = new ImmutableSRGBColor(188, 143, 143);

  /** The Constant royalblue. */
  public static final ImmutableSRGBColor royalblue = new ImmutableSRGBColor(65, 105, 225);

  /** The Constant saddlebrown. */
  public static final ImmutableSRGBColor saddlebrown = new ImmutableSRGBColor(139, 69, 19);

  /** The Constant salmon. */
  public static final ImmutableSRGBColor salmon = new ImmutableSRGBColor(250, 128, 114);

  /** The Constant sandybrown. */
  public static final ImmutableSRGBColor sandybrown = new ImmutableSRGBColor(244, 164, 96);

  /** The Constant seagreen. */
  public static final ImmutableSRGBColor seagreen = new ImmutableSRGBColor(46, 139, 87);

  /** The Constant seashell. */
  public static final ImmutableSRGBColor seashell = new ImmutableSRGBColor(255, 245, 238);

  /** The Constant sienna. */
  public static final ImmutableSRGBColor sienna = new ImmutableSRGBColor(160, 82, 45);

  /** The Constant silver. */
  public static final ImmutableSRGBColor silver = new ImmutableSRGBColor(192, 192, 192);

  /** The Constant skyblue. */
  public static final ImmutableSRGBColor skyblue = new ImmutableSRGBColor(135, 206, 235);

  /** The Constant slateblue. */
  public static final ImmutableSRGBColor slateblue = new ImmutableSRGBColor(106, 90, 205);

  /** The Constant slategray. */
  public static final ImmutableSRGBColor slategray = new ImmutableSRGBColor(112, 128, 144);

  /** The Constant slategrey. */
  public static final ImmutableSRGBColor slategrey = new ImmutableSRGBColor(112, 128, 144);

  /** The Constant snow. */
  public static final ImmutableSRGBColor snow = new ImmutableSRGBColor(255, 250, 250);

  /** The Constant springgreen. */
  public static final ImmutableSRGBColor springgreen = new ImmutableSRGBColor(0, 255, 127);

  /** The Constant steelblue. */
  public static final ImmutableSRGBColor steelblue = new ImmutableSRGBColor(70, 130, 180);

  /** The Constant tan. */
  public static final ImmutableSRGBColor tan = new ImmutableSRGBColor(210, 180, 140);

  /** The Constant teal. */
  public static final ImmutableSRGBColor teal = new ImmutableSRGBColor(0, 128, 128);

  /** The Constant thistle. */
  public static final ImmutableSRGBColor thistle = new ImmutableSRGBColor(216, 191, 216);

  /** The Constant tomato. */
  public static final ImmutableSRGBColor tomato = new ImmutableSRGBColor(255, 99, 71);

  /** The Constant turquoise. */
  public static final ImmutableSRGBColor turquoise = new ImmutableSRGBColor(64, 224, 208);

  /** The Constant violet. */
  public static final ImmutableSRGBColor violet = new ImmutableSRGBColor(238, 130, 238);

  /** The Constant wheat. */
  public static final ImmutableSRGBColor wheat = new ImmutableSRGBColor(245, 222, 179);

  /** The Constant white. */
  public static final ImmutableSRGBColor white = new ImmutableSRGBColor(255, 255, 255);

  /** The Constant whitesmoke. */
  public static final ImmutableSRGBColor whitesmoke = new ImmutableSRGBColor(245, 245, 245);

  /** The Constant yellow. */
  public static final ImmutableSRGBColor yellow = new ImmutableSRGBColor(255, 255, 0);

  /** The Constant yellowgreen. */
  public static final ImmutableSRGBColor yellowgreen = new ImmutableSRGBColor(154, 205, 50);

  /** The Constant colormap. */
  private static final HashMap<ICaseString, ImmutableSRGBColor> colormap = new HashMap<ICaseString, ImmutableSRGBColor>();

  static {
    SRGBColor.colormap.put(new ICaseString("aliceblue"), SRGBColor.aliceblue);
    SRGBColor.colormap.put(new ICaseString("antiquewhite"), SRGBColor.antiquewhite);
    SRGBColor.colormap.put(new ICaseString("aqua"), SRGBColor.aqua);
    SRGBColor.colormap.put(new ICaseString("aquamarine"), SRGBColor.aquamarine);
    SRGBColor.colormap.put(new ICaseString("azure"), SRGBColor.azure);
    SRGBColor.colormap.put(new ICaseString("beige"), SRGBColor.beige);
    SRGBColor.colormap.put(new ICaseString("bisque"), SRGBColor.bisque);
    SRGBColor.colormap.put(new ICaseString("black"), SRGBColor.black);
    SRGBColor.colormap.put(new ICaseString("blanchedalmond"), SRGBColor.blanchedalmond);
    SRGBColor.colormap.put(new ICaseString("blue"), SRGBColor.blue);
    SRGBColor.colormap.put(new ICaseString("blueviolet"), SRGBColor.blueviolet);
    SRGBColor.colormap.put(new ICaseString("brown"), SRGBColor.brown);
    SRGBColor.colormap.put(new ICaseString("burlywood"), SRGBColor.burlywood);
    SRGBColor.colormap.put(new ICaseString("cadetblue"), SRGBColor.cadetblue);
    SRGBColor.colormap.put(new ICaseString("chartreuse"), SRGBColor.chartreuse);
    SRGBColor.colormap.put(new ICaseString("chocolate"), SRGBColor.chocolate);
    SRGBColor.colormap.put(new ICaseString("coral"), SRGBColor.coral);
    SRGBColor.colormap.put(new ICaseString("cornflowerblue"), SRGBColor.cornflowerblue);
    SRGBColor.colormap.put(new ICaseString("cornsilk"), SRGBColor.cornsilk);
    SRGBColor.colormap.put(new ICaseString("crimson"), SRGBColor.crimson);
    SRGBColor.colormap.put(new ICaseString("cyan"), SRGBColor.cyan);
    SRGBColor.colormap.put(new ICaseString("darkblue"), SRGBColor.darkblue);
    SRGBColor.colormap.put(new ICaseString("darkcyan"), SRGBColor.darkcyan);
    SRGBColor.colormap.put(new ICaseString("darkgoldenrod"), SRGBColor.darkgoldenrod);
    SRGBColor.colormap.put(new ICaseString("darkgray"), SRGBColor.darkgray);
    SRGBColor.colormap.put(new ICaseString("darkgreen"), SRGBColor.darkgreen);
    SRGBColor.colormap.put(new ICaseString("darkgrey"), SRGBColor.darkgrey);
    SRGBColor.colormap.put(new ICaseString("darkkhaki"), SRGBColor.darkkhaki);
    SRGBColor.colormap.put(new ICaseString("darkmagenta"), SRGBColor.darkmagenta);
    SRGBColor.colormap.put(new ICaseString("darkolivegreen"), SRGBColor.darkolivegreen);
    SRGBColor.colormap.put(new ICaseString("darkorange"), SRGBColor.darkorange);
    SRGBColor.colormap.put(new ICaseString("darkorchid"), SRGBColor.darkorchid);
    SRGBColor.colormap.put(new ICaseString("darkred"), SRGBColor.darkred);
    SRGBColor.colormap.put(new ICaseString("darksalmon"), SRGBColor.darksalmon);
    SRGBColor.colormap.put(new ICaseString("darkseagreen"), SRGBColor.darkseagreen);
    SRGBColor.colormap.put(new ICaseString("darkslateblue"), SRGBColor.darkslateblue);
    SRGBColor.colormap.put(new ICaseString("darkslategray"), SRGBColor.darkslategray);
    SRGBColor.colormap.put(new ICaseString("darkslategrey"), SRGBColor.darkslategrey);
    SRGBColor.colormap.put(new ICaseString("darkturquoise"), SRGBColor.darkturquoise);
    SRGBColor.colormap.put(new ICaseString("darkviolet"), SRGBColor.darkviolet);
    SRGBColor.colormap.put(new ICaseString("deeppink"), SRGBColor.deeppink);
    SRGBColor.colormap.put(new ICaseString("deepskyblue"), SRGBColor.deepskyblue);
    SRGBColor.colormap.put(new ICaseString("dimgray"), SRGBColor.dimgray);
    SRGBColor.colormap.put(new ICaseString("dimgrey"), SRGBColor.dimgrey);
    SRGBColor.colormap.put(new ICaseString("dodgerblue"), SRGBColor.dodgerblue);
    SRGBColor.colormap.put(new ICaseString("firebrick"), SRGBColor.firebrick);
    SRGBColor.colormap.put(new ICaseString("floralwhite"), SRGBColor.floralwhite);
    SRGBColor.colormap.put(new ICaseString("forestgreen"), SRGBColor.forestgreen);
    SRGBColor.colormap.put(new ICaseString("fuchsia"), SRGBColor.fuchsia);
    SRGBColor.colormap.put(new ICaseString("gainsboro"), SRGBColor.gainsboro);
    SRGBColor.colormap.put(new ICaseString("ghostwhite"), SRGBColor.ghostwhite);
    SRGBColor.colormap.put(new ICaseString("gold"), SRGBColor.gold);
    SRGBColor.colormap.put(new ICaseString("goldenrod"), SRGBColor.goldenrod);
    SRGBColor.colormap.put(new ICaseString("gray"), SRGBColor.gray);
    SRGBColor.colormap.put(new ICaseString("green"), SRGBColor.green);
    SRGBColor.colormap.put(new ICaseString("greenyellow"), SRGBColor.greenyellow);
    SRGBColor.colormap.put(new ICaseString("grey"), SRGBColor.grey);
    SRGBColor.colormap.put(new ICaseString("honeydew"), SRGBColor.honeydew);
    SRGBColor.colormap.put(new ICaseString("hotpink"), SRGBColor.hotpink);
    SRGBColor.colormap.put(new ICaseString("indianred"), SRGBColor.indianred);
    SRGBColor.colormap.put(new ICaseString("indigo"), SRGBColor.indigo);
    SRGBColor.colormap.put(new ICaseString("ivory"), SRGBColor.ivory);
    SRGBColor.colormap.put(new ICaseString("khaki"), SRGBColor.khaki);
    SRGBColor.colormap.put(new ICaseString("lavender"), SRGBColor.lavender);
    SRGBColor.colormap.put(new ICaseString("lavenderblush"), SRGBColor.lavenderblush);
    SRGBColor.colormap.put(new ICaseString("lawngreen"), SRGBColor.lawngreen);
    SRGBColor.colormap.put(new ICaseString("lemonchiffon"), SRGBColor.lemonchiffon);
    SRGBColor.colormap.put(new ICaseString("lightblue"), SRGBColor.lightblue);
    SRGBColor.colormap.put(new ICaseString("lightcoral"), SRGBColor.lightcoral);
    SRGBColor.colormap.put(new ICaseString("lightcyan"), SRGBColor.lightcyan);
    SRGBColor.colormap.put(new ICaseString("lightgoldenrodyellow"), SRGBColor.lightgoldenrodyellow);
    SRGBColor.colormap.put(new ICaseString("lightgray"), SRGBColor.lightgray);
    SRGBColor.colormap.put(new ICaseString("lightgreen"), SRGBColor.lightgreen);
    SRGBColor.colormap.put(new ICaseString("lightgrey"), SRGBColor.lightgrey);
    SRGBColor.colormap.put(new ICaseString("lightpink"), SRGBColor.lightpink);
    SRGBColor.colormap.put(new ICaseString("lightsalmon"), SRGBColor.lightsalmon);
    SRGBColor.colormap.put(new ICaseString("lightseagreen"), SRGBColor.lightseagreen);
    SRGBColor.colormap.put(new ICaseString("lightskyblue"), SRGBColor.lightskyblue);
    SRGBColor.colormap.put(new ICaseString("lightslategray"), SRGBColor.lightslategray);
    SRGBColor.colormap.put(new ICaseString("lightslategrey"), SRGBColor.lightslategrey);
    SRGBColor.colormap.put(new ICaseString("lightsteelblue"), SRGBColor.lightsteelblue);
    SRGBColor.colormap.put(new ICaseString("lightyellow"), SRGBColor.lightyellow);
    SRGBColor.colormap.put(new ICaseString("lime"), SRGBColor.lime);
    SRGBColor.colormap.put(new ICaseString("limegreen"), SRGBColor.limegreen);
    SRGBColor.colormap.put(new ICaseString("linen"), SRGBColor.linen);
    SRGBColor.colormap.put(new ICaseString("magenta"), SRGBColor.magenta);
    SRGBColor.colormap.put(new ICaseString("maroon"), SRGBColor.maroon);
    SRGBColor.colormap.put(new ICaseString("mediumaquamarine"), SRGBColor.mediumaquamarine);
    SRGBColor.colormap.put(new ICaseString("mediumblue"), SRGBColor.mediumblue);
    SRGBColor.colormap.put(new ICaseString("mediumorchid"), SRGBColor.mediumorchid);
    SRGBColor.colormap.put(new ICaseString("mediumpurple"), SRGBColor.mediumpurple);
    SRGBColor.colormap.put(new ICaseString("mediumseagreen"), SRGBColor.mediumseagreen);
    SRGBColor.colormap.put(new ICaseString("mediumslateblue"), SRGBColor.mediumslateblue);
    SRGBColor.colormap.put(new ICaseString("mediumspringgreen"), SRGBColor.mediumspringgreen);
    SRGBColor.colormap.put(new ICaseString("mediumturquoise"), SRGBColor.mediumturquoise);
    SRGBColor.colormap.put(new ICaseString("mediumvioletred"), SRGBColor.mediumvioletred);
    SRGBColor.colormap.put(new ICaseString("midnightblue"), SRGBColor.midnightblue);
    SRGBColor.colormap.put(new ICaseString("mintcream"), SRGBColor.mintcream);
    SRGBColor.colormap.put(new ICaseString("mistyrose"), SRGBColor.mistyrose);
    SRGBColor.colormap.put(new ICaseString("moccasin"), SRGBColor.moccasin);
    SRGBColor.colormap.put(new ICaseString("navajowhite"), SRGBColor.navajowhite);
    SRGBColor.colormap.put(new ICaseString("navy"), SRGBColor.navy);
    SRGBColor.colormap.put(new ICaseString("oldlace"), SRGBColor.oldlace);
    SRGBColor.colormap.put(new ICaseString("olive"), SRGBColor.olive);
    SRGBColor.colormap.put(new ICaseString("olivedrab"), SRGBColor.olivedrab);
    SRGBColor.colormap.put(new ICaseString("orange"), SRGBColor.orange);
    SRGBColor.colormap.put(new ICaseString("orangered"), SRGBColor.orangered);
    SRGBColor.colormap.put(new ICaseString("orchid"), SRGBColor.orchid);
    SRGBColor.colormap.put(new ICaseString("palegoldenrod"), SRGBColor.palegoldenrod);
    SRGBColor.colormap.put(new ICaseString("palegreen"), SRGBColor.palegreen);
    SRGBColor.colormap.put(new ICaseString("paleturquoise"), SRGBColor.paleturquoise);
    SRGBColor.colormap.put(new ICaseString("palevioletred"), SRGBColor.palevioletred);
    SRGBColor.colormap.put(new ICaseString("papayawhip"), SRGBColor.papayawhip);
    SRGBColor.colormap.put(new ICaseString("peachpuff"), SRGBColor.peachpuff);
    SRGBColor.colormap.put(new ICaseString("peru"), SRGBColor.peru);
    SRGBColor.colormap.put(new ICaseString("pink"), SRGBColor.pink);
    SRGBColor.colormap.put(new ICaseString("plum"), SRGBColor.plum);
    SRGBColor.colormap.put(new ICaseString("powderblue"), SRGBColor.powderblue);
    SRGBColor.colormap.put(new ICaseString("purple"), SRGBColor.purple);
    SRGBColor.colormap.put(new ICaseString("red"), SRGBColor.red);
    SRGBColor.colormap.put(new ICaseString("rosybrown"), SRGBColor.rosybrown);
    SRGBColor.colormap.put(new ICaseString("royalblue"), SRGBColor.royalblue);
    SRGBColor.colormap.put(new ICaseString("saddlebrown"), SRGBColor.saddlebrown);
    SRGBColor.colormap.put(new ICaseString("salmon"), SRGBColor.salmon);
    SRGBColor.colormap.put(new ICaseString("sandybrown"), SRGBColor.sandybrown);
    SRGBColor.colormap.put(new ICaseString("seagreen"), SRGBColor.seagreen);
    SRGBColor.colormap.put(new ICaseString("seashell"), SRGBColor.seashell);
    SRGBColor.colormap.put(new ICaseString("sienna"), SRGBColor.sienna);
    SRGBColor.colormap.put(new ICaseString("silver"), SRGBColor.silver);
    SRGBColor.colormap.put(new ICaseString("skyblue"), SRGBColor.skyblue);
    SRGBColor.colormap.put(new ICaseString("slateblue"), SRGBColor.slateblue);
    SRGBColor.colormap.put(new ICaseString("slategray"), SRGBColor.slategray);
    SRGBColor.colormap.put(new ICaseString("slategrey"), SRGBColor.slategrey);
    SRGBColor.colormap.put(new ICaseString("snow"), SRGBColor.snow);
    SRGBColor.colormap.put(new ICaseString("springgreen"), SRGBColor.springgreen);
    SRGBColor.colormap.put(new ICaseString("steelblue"), SRGBColor.steelblue);
    SRGBColor.colormap.put(new ICaseString("tan"), SRGBColor.tan);
    SRGBColor.colormap.put(new ICaseString("teal"), SRGBColor.teal);
    SRGBColor.colormap.put(new ICaseString("thistle"), SRGBColor.thistle);
    SRGBColor.colormap.put(new ICaseString("tomato"), SRGBColor.tomato);
    SRGBColor.colormap.put(new ICaseString("turquoise"), SRGBColor.turquoise);
    SRGBColor.colormap.put(new ICaseString("violet"), SRGBColor.violet);
    SRGBColor.colormap.put(new ICaseString("wheat"), SRGBColor.wheat);
    SRGBColor.colormap.put(new ICaseString("white"), SRGBColor.white);
    SRGBColor.colormap.put(new ICaseString("whitesmoke"), SRGBColor.whitesmoke);
    SRGBColor.colormap.put(new ICaseString("yellow"), SRGBColor.yellow);
    SRGBColor.colormap.put(new ICaseString("yellowgreen"), SRGBColor.yellowgreen);
  }

  /**
   * Gets the color by its name.
   * 
   * @param name the name
   * @return the color or null if not found
   */
  public static ImmutableSRGBColor getColorByName(final ICaseString name) {
    final ImmutableSRGBColor c = SRGBColor.colormap.get(name);
    if (c == null) {
      return null;
    } else {
      return c;
    }
  }

  /**
   * Gets a string representation of the color in CSV format using colon.
   * 
   * @return the string
   */
  public String asCSVString() {
    return "" + getR() + ";" + getG() + ";" + getB();
  }

  /**
   * Compare colors without taking into account the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the
   * lightness (L) component.
   * 
   * @param other the other color to compare with
   * @return the distance
   */
  public double distanceABInCIELab(final SRGBColor other) {
    final double[] lab1 = ColorSpaceUtil.SRGBToLab(getR(), getG(), getB());
    final double[] lab2 = ColorSpaceUtil.SRGBToLab(other.getR(), other.getG(), other.getB());
    return CIELabUtil.distanceAB(lab1, lab2);
  }

  /**
   * Compare colors using the delta E (CIE 1976) distance.
   * 
   * @param other the other color to compare with
   * @return the distance
   */
  public double distanceLABInCIELab(final SRGBColor other) {
    final double[] lab1 = ColorSpaceUtil.SRGBToLab(getR(), getG(), getB());
    final double[] lab2 = ColorSpaceUtil.SRGBToLab(other.getR(), other.getG(), other.getB());
    return CIELabUtil.distanceLAB(lab1, lab2);
  }

  /**
   * Compare colors taking into account only the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the a and b
   * components.
   * 
   * @param other the other color to compare with
   * @return the distance
   */
  public double distanceLInCIELab(final SRGBColor other) {
    final double[] lab1 = ColorSpaceUtil.SRGBToLab(getR(), getG(), getB());
    final double[] lab2 = ColorSpaceUtil.SRGBToLab(other.getR(), other.getG(), other.getB());
    return CIELabUtil.distanceL(lab1, lab2);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof SRGBColor) {
      final SRGBColor other = (SRGBColor) obj;
      return getR() == other.getR() && getG() == other.getG() && getB() == other.getB();
    }
    return false;
  }

  /**
   * Gets the.
   * 
   * @return the int[]
   */
  public int[] get() {
    return new int[] { getR(), getG(), getB() };
  }

  /**
   * Gets the b.
   * 
   * @return the b
   */
  public abstract int getB();

  /**
   * Gets the g.
   * 
   * @return the g
   */
  public abstract int getG();

  /**
   * Gets the r.
   * 
   * @return the r
   */
  public abstract int getR();

  /**
   * Gets the W3C luminance as stated in WCAG.
   * 
   * @return the luminance
   */
  public double getW3CLuminance() {
    return W3CUtil.getsRGBLuminance(getR(), getG(), getB());
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return (getR() * 256 + getG()) * 256 + getB();
  }

  /**
   * Simulate dichromacy deficiency.
   * 
   * @param <T> the generic type
   * @param dichromacyDeficiency the dichromacy deficiency
   * @param clazz the clazz
   * @return the SRGB color
   * @see DichromacyDeficiency#kuhnSimulation(DichromacyDeficiency, double[])
   */
  public <T extends SRGBColor> T kuhnSimulationOfDichromacy(final DichromacyDeficiency dichromacyDeficiency,
      final Class<T> clazz) {
    final double[] labBefore = ColorSpaceUtil.XYZToLab(ColorSpaceUtil.SRGBToXYZ(getR(), getG(), getB()));
    final double[] labAfter = DichromacyDeficiency.kuhnSimulation(dichromacyDeficiency, labBefore);
    final int[] rgbAfter = ColorSpaceUtil.XYZToSRGB(ColorSpaceUtil.LabToXYZ(labAfter));

    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(rgbAfter[0], rgbAfter[1], rgbAfter[2]);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /**
   * Convert the color into a CIELAB color.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the color
   */
  public <T extends CIELabColor> T toCIELab(final Class<T> clazz) {
    final double[] lab = ColorSpaceUtil.SRGBToLab(getR(), getG(), getB());
    try {
      final Constructor<T> c = clazz.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
      return c.newInstance(lab[0], lab[1], lab[2]);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /**
   * Convert the color into a CIEXYZ color.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the color
   */
  public <T extends CIEXYZColor> T toCIEXYZ(final Class<T> clazz) {
    final double[] xyz = ColorSpaceUtil.SRGBToXYZ(getR(), getG(), getB());
    try {
      final Constructor<T> c = clazz.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
      return c.newInstance(xyz[0], xyz[1], xyz[2]);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return ColorSpaceUtil.toString(getR(), getG(), getB());
  }
}
