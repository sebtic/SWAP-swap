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
 * <http://www.gnu.org/licenses/>. $Id: SRGBColorRegistry.java 90 2011-06-21
 * 17:29:12Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver.color;

import java.util.HashMap;
import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The registry of RGB colors.
 */
public final class SRGBColorRegistry {

  /** The Constant aliceblue. */
  public static final SRGBColor aliceblue = new SRGBColor(240, 248, 255);

  /** The Constant antiquewhite. */
  public static final SRGBColor antiquewhite = new SRGBColor(250, 235, 215);

  /** The Constant aqua. */
  public static final SRGBColor aqua = new SRGBColor(0, 255, 255);

  /** The Constant aquamarine. */
  public static final SRGBColor aquamarine = new SRGBColor(127, 255, 212);

  /** The Constant azure. */
  public static final SRGBColor azure = new SRGBColor(240, 255, 255);

  /** The Constant beige. */
  public static final SRGBColor beige = new SRGBColor(245, 245, 220);

  /** The Constant bisque. */
  public static final SRGBColor bisque = new SRGBColor(255, 228, 196);

  /** The Constant black. */
  public static final SRGBColor black = new SRGBColor(0, 0, 0);

  /** The Constant blanchedalmond. */
  public static final SRGBColor blanchedalmond = new SRGBColor(255, 235, 205);

  /** The Constant blue. */
  public static final SRGBColor blue = new SRGBColor(0, 0, 255);

  /** The Constant blueviolet. */
  public static final SRGBColor blueviolet = new SRGBColor(138, 43, 226);

  /** The Constant brown. */
  public static final SRGBColor brown = new SRGBColor(165, 42, 42);

  /** The Constant burlywood. */
  public static final SRGBColor burlywood = new SRGBColor(222, 184, 135);

  /** The Constant cadetblue. */
  public static final SRGBColor cadetblue = new SRGBColor(95, 158, 160);

  /** The Constant chartreuse. */
  public static final SRGBColor chartreuse = new SRGBColor(127, 255, 0);

  /** The Constant chocolate. */
  public static final SRGBColor chocolate = new SRGBColor(210, 105, 30);

  /** The Constant coral. */
  public static final SRGBColor coral = new SRGBColor(255, 127, 80);

  /** The Constant cornflowerblue. */
  public static final SRGBColor cornflowerblue = new SRGBColor(100, 149, 237);

  /** The Constant cornsilk. */
  public static final SRGBColor cornsilk = new SRGBColor(255, 248, 220);

  /** The Constant crimson. */
  public static final SRGBColor crimson = new SRGBColor(220, 20, 60);

  /** The Constant cyan. */
  public static final SRGBColor cyan = new SRGBColor(0, 255, 255);

  /** The Constant darkblue. */
  public static final SRGBColor darkblue = new SRGBColor(0, 0, 139);

  /** The Constant darkcyan. */
  public static final SRGBColor darkcyan = new SRGBColor(0, 139, 139);

  /** The Constant darkgoldenrod. */
  public static final SRGBColor darkgoldenrod = new SRGBColor(184, 134, 11);

  /** The Constant darkgray. */
  public static final SRGBColor darkgray = new SRGBColor(169, 169, 169);

  /** The Constant darkgreen. */
  public static final SRGBColor darkgreen = new SRGBColor(0, 100, 0);

  /** The Constant darkgrey. */
  public static final SRGBColor darkgrey = new SRGBColor(169, 169, 169);

  /** The Constant darkkhaki. */
  public static final SRGBColor darkkhaki = new SRGBColor(189, 183, 107);

  /** The Constant darkmagenta. */
  public static final SRGBColor darkmagenta = new SRGBColor(139, 0, 139);

  /** The Constant darkolivegreen. */
  public static final SRGBColor darkolivegreen = new SRGBColor(85, 107, 47);

  /** The Constant darkorange. */
  public static final SRGBColor darkorange = new SRGBColor(255, 140, 0);

  /** The Constant darkorchid. */
  public static final SRGBColor darkorchid = new SRGBColor(153, 50, 204);

  /** The Constant darkred. */
  public static final SRGBColor darkred = new SRGBColor(139, 0, 0);

  /** The Constant darksalmon. */
  public static final SRGBColor darksalmon = new SRGBColor(233, 150, 122);

  /** The Constant darkseagreen. */
  public static final SRGBColor darkseagreen = new SRGBColor(143, 188, 143);

  /** The Constant darkslateblue. */
  public static final SRGBColor darkslateblue = new SRGBColor(72, 61, 139);

  /** The Constant darkslategray. */
  public static final SRGBColor darkslategray = new SRGBColor(47, 79, 79);

  /** The Constant darkslategrey. */
  public static final SRGBColor darkslategrey = new SRGBColor(47, 79, 79);

  /** The Constant darkturquoise. */
  public static final SRGBColor darkturquoise = new SRGBColor(0, 206, 209);

  /** The Constant darkviolet. */
  public static final SRGBColor darkviolet = new SRGBColor(148, 0, 211);

  /** The Constant deeppink. */
  public static final SRGBColor deeppink = new SRGBColor(255, 20, 147);

  /** The Constant deepskyblue. */
  public static final SRGBColor deepskyblue = new SRGBColor(0, 191, 255);

  /** The Constant dimgray. */
  public static final SRGBColor dimgray = new SRGBColor(105, 105, 105);

  /** The Constant dimgrey. */
  public static final SRGBColor dimgrey = new SRGBColor(105, 105, 105);

  /** The Constant dodgerblue. */
  public static final SRGBColor dodgerblue = new SRGBColor(30, 144, 255);

  /** The Constant firebrick. */
  public static final SRGBColor firebrick = new SRGBColor(178, 34, 34);

  /** The Constant floralwhite. */
  public static final SRGBColor floralwhite = new SRGBColor(255, 250, 240);

  /** The Constant forestgreen. */
  public static final SRGBColor forestgreen = new SRGBColor(34, 139, 34);

  /** The Constant fuchsia. */
  public static final SRGBColor fuchsia = new SRGBColor(255, 0, 255);

  /** The Constant gainsboro. */
  public static final SRGBColor gainsboro = new SRGBColor(220, 220, 220);

  /** The Constant ghostwhite. */
  public static final SRGBColor ghostwhite = new SRGBColor(248, 248, 255);

  /** The Constant gold. */
  public static final SRGBColor gold = new SRGBColor(255, 215, 0);

  /** The Constant goldenrod. */
  public static final SRGBColor goldenrod = new SRGBColor(218, 165, 32);

  /** The Constant gray. */
  public static final SRGBColor gray = new SRGBColor(128, 128, 128);

  /** The Constant green. */
  public static final SRGBColor green = new SRGBColor(0, 128, 0);

  /** The Constant greenyellow. */
  public static final SRGBColor greenyellow = new SRGBColor(173, 255, 47);

  /** The Constant grey. */
  public static final SRGBColor grey = new SRGBColor(128, 128, 128);

  /** The Constant honeydew. */
  public static final SRGBColor honeydew = new SRGBColor(240, 255, 240);

  /** The Constant hotpink. */
  public static final SRGBColor hotpink = new SRGBColor(255, 105, 180);

  /** The Constant indianred. */
  public static final SRGBColor indianred = new SRGBColor(205, 92, 92);

  /** The Constant indigo. */
  public static final SRGBColor indigo = new SRGBColor(75, 0, 130);

  /** The Constant ivory. */
  public static final SRGBColor ivory = new SRGBColor(255, 255, 240);

  /** The Constant khaki. */
  public static final SRGBColor khaki = new SRGBColor(240, 230, 140);

  /** The Constant lavender. */
  public static final SRGBColor lavender = new SRGBColor(230, 230, 250);

  /** The Constant lavenderblush. */
  public static final SRGBColor lavenderblush = new SRGBColor(255, 240, 245);

  /** The Constant lawngreen. */
  public static final SRGBColor lawngreen = new SRGBColor(124, 252, 0);

  /** The Constant lemonchiffon. */
  public static final SRGBColor lemonchiffon = new SRGBColor(255, 250, 205);

  /** The Constant lightblue. */
  public static final SRGBColor lightblue = new SRGBColor(173, 216, 230);

  /** The Constant lightcoral. */
  public static final SRGBColor lightcoral = new SRGBColor(240, 128, 128);

  /** The Constant lightcyan. */
  public static final SRGBColor lightcyan = new SRGBColor(224, 255, 255);

  /** The Constant lightgoldenrodyellow. */
  public static final SRGBColor lightgoldenrodyellow = new SRGBColor(250, 250, 210);

  /** The Constant lightgray. */
  public static final SRGBColor lightgray = new SRGBColor(211, 211, 211);

  /** The Constant lightgreen. */
  public static final SRGBColor lightgreen = new SRGBColor(144, 238, 144);

  /** The Constant lightgrey. */
  public static final SRGBColor lightgrey = new SRGBColor(211, 211, 211);

  /** The Constant lightpink. */
  public static final SRGBColor lightpink = new SRGBColor(255, 182, 193);

  /** The Constant lightsalmon. */
  public static final SRGBColor lightsalmon = new SRGBColor(255, 160, 122);

  /** The Constant lightseagreen. */
  public static final SRGBColor lightseagreen = new SRGBColor(32, 178, 170);

  /** The Constant lightskyblue. */
  public static final SRGBColor lightskyblue = new SRGBColor(135, 206, 250);

  /** The Constant lightslategray. */
  public static final SRGBColor lightslategray = new SRGBColor(119, 136, 153);

  /** The Constant lightslategrey. */
  public static final SRGBColor lightslategrey = new SRGBColor(119, 136, 153);

  /** The Constant lightsteelblue. */
  public static final SRGBColor lightsteelblue = new SRGBColor(176, 196, 222);

  /** The Constant lightyellow. */
  public static final SRGBColor lightyellow = new SRGBColor(255, 255, 224);

  /** The Constant lime. */
  public static final SRGBColor lime = new SRGBColor(0, 255, 0);

  /** The Constant limegreen. */
  public static final SRGBColor limegreen = new SRGBColor(50, 205, 50);

  /** The Constant linen. */
  public static final SRGBColor linen = new SRGBColor(250, 240, 230);

  /** The Constant magenta. */
  public static final SRGBColor magenta = new SRGBColor(255, 0, 255);

  /** The Constant maroon. */
  public static final SRGBColor maroon = new SRGBColor(128, 0, 0);

  /** The Constant mediumaquamarine. */
  public static final SRGBColor mediumaquamarine = new SRGBColor(102, 205, 170);

  /** The Constant mediumblue. */
  public static final SRGBColor mediumblue = new SRGBColor(0, 0, 205);

  /** The Constant mediumorchid. */
  public static final SRGBColor mediumorchid = new SRGBColor(186, 85, 211);

  /** The Constant mediumpurple. */
  public static final SRGBColor mediumpurple = new SRGBColor(147, 112, 219);

  /** The Constant mediumseagreen. */
  public static final SRGBColor mediumseagreen = new SRGBColor(60, 179, 113);

  /** The Constant mediumslateblue. */
  public static final SRGBColor mediumslateblue = new SRGBColor(123, 104, 238);

  /** The Constant mediumspringgreen. */
  public static final SRGBColor mediumspringgreen = new SRGBColor(0, 250, 154);

  /** The Constant mediumturquoise. */
  public static final SRGBColor mediumturquoise = new SRGBColor(72, 209, 204);

  /** The Constant mediumvioletred. */
  public static final SRGBColor mediumvioletred = new SRGBColor(199, 21, 133);

  /** The Constant midnightblue. */
  public static final SRGBColor midnightblue = new SRGBColor(25, 25, 112);

  /** The Constant mintcream. */
  public static final SRGBColor mintcream = new SRGBColor(245, 255, 250);

  /** The Constant mistyrose. */
  public static final SRGBColor mistyrose = new SRGBColor(255, 228, 225);

  /** The Constant moccasin. */
  public static final SRGBColor moccasin = new SRGBColor(255, 228, 181);

  /** The Constant navajowhite. */
  public static final SRGBColor navajowhite = new SRGBColor(255, 222, 173);

  /** The Constant navy. */
  public static final SRGBColor navy = new SRGBColor(0, 0, 128);

  /** The Constant oldlace. */
  public static final SRGBColor oldlace = new SRGBColor(253, 245, 230);

  /** The Constant olive. */
  public static final SRGBColor olive = new SRGBColor(128, 128, 0);

  /** The Constant olivedrab. */
  public static final SRGBColor olivedrab = new SRGBColor(107, 142, 35);

  /** The Constant orange. */
  public static final SRGBColor orange = new SRGBColor(255, 165, 0);

  /** The Constant orangered. */
  public static final SRGBColor orangered = new SRGBColor(255, 69, 0);

  /** The Constant orchid. */
  public static final SRGBColor orchid = new SRGBColor(218, 112, 214);

  /** The Constant palegoldenrod. */
  public static final SRGBColor palegoldenrod = new SRGBColor(238, 232, 170);

  /** The Constant palegreen. */
  public static final SRGBColor palegreen = new SRGBColor(152, 251, 152);

  /** The Constant paleturquoise. */
  public static final SRGBColor paleturquoise = new SRGBColor(175, 238, 238);

  /** The Constant palevioletred. */
  public static final SRGBColor palevioletred = new SRGBColor(219, 112, 147);

  /** The Constant papayawhip. */
  public static final SRGBColor papayawhip = new SRGBColor(255, 239, 213);

  /** The Constant peachpuff. */
  public static final SRGBColor peachpuff = new SRGBColor(255, 218, 185);

  /** The Constant peru. */
  public static final SRGBColor peru = new SRGBColor(205, 133, 63);

  /** The Constant pink. */
  public static final SRGBColor pink = new SRGBColor(255, 192, 203);

  /** The Constant plum. */
  public static final SRGBColor plum = new SRGBColor(221, 160, 221);

  /** The Constant powderblue. */
  public static final SRGBColor powderblue = new SRGBColor(176, 224, 230);

  /** The Constant purple. */
  public static final SRGBColor purple = new SRGBColor(128, 0, 128);

  /** The Constant red. */
  public static final SRGBColor red = new SRGBColor(255, 0, 0);

  /** The Constant rosybrown. */
  public static final SRGBColor rosybrown = new SRGBColor(188, 143, 143);

  /** The Constant royalblue. */
  public static final SRGBColor royalblue = new SRGBColor(65, 105, 225);

  /** The Constant saddlebrown. */
  public static final SRGBColor saddlebrown = new SRGBColor(139, 69, 19);

  /** The Constant salmon. */
  public static final SRGBColor salmon = new SRGBColor(250, 128, 114);

  /** The Constant sandybrown. */
  public static final SRGBColor sandybrown = new SRGBColor(244, 164, 96);

  /** The Constant seagreen. */
  public static final SRGBColor seagreen = new SRGBColor(46, 139, 87);

  /** The Constant seashell. */
  public static final SRGBColor seashell = new SRGBColor(255, 245, 238);

  /** The Constant sienna. */
  public static final SRGBColor sienna = new SRGBColor(160, 82, 45);

  /** The Constant silver. */
  public static final SRGBColor silver = new SRGBColor(192, 192, 192);

  /** The Constant skyblue. */
  public static final SRGBColor skyblue = new SRGBColor(135, 206, 235);

  /** The Constant slateblue. */
  public static final SRGBColor slateblue = new SRGBColor(106, 90, 205);

  /** The Constant slategray. */
  public static final SRGBColor slategray = new SRGBColor(112, 128, 144);

  /** The Constant slategrey. */
  public static final SRGBColor slategrey = new SRGBColor(112, 128, 144);

  /** The Constant snow. */
  public static final SRGBColor snow = new SRGBColor(255, 250, 250);

  /** The Constant springgreen. */
  public static final SRGBColor springgreen = new SRGBColor(0, 255, 127);

  /** The Constant steelblue. */
  public static final SRGBColor steelblue = new SRGBColor(70, 130, 180);

  /** The Constant tan. */
  public static final SRGBColor tan = new SRGBColor(210, 180, 140);

  /** The Constant teal. */
  public static final SRGBColor teal = new SRGBColor(0, 128, 128);

  /** The Constant thistle. */
  public static final SRGBColor thistle = new SRGBColor(216, 191, 216);

  /** The Constant tomato. */
  public static final SRGBColor tomato = new SRGBColor(255, 99, 71);

  /** The Constant turquoise. */
  public static final SRGBColor turquoise = new SRGBColor(64, 224, 208);

  /** The Constant violet. */
  public static final SRGBColor violet = new SRGBColor(238, 130, 238);

  /** The Constant wheat. */
  public static final SRGBColor wheat = new SRGBColor(245, 222, 179);

  /** The Constant white. */
  public static final SRGBColor white = new SRGBColor(255, 255, 255);

  /** The Constant whitesmoke. */
  public static final SRGBColor whitesmoke = new SRGBColor(245, 245, 245);

  /** The Constant yellow. */
  public static final SRGBColor yellow = new SRGBColor(255, 255, 0);

  /** The Constant yellowgreen. */
  public static final SRGBColor yellowgreen = new SRGBColor(154, 205, 50);

  /** The Constant colormap. */
  private static final HashMap<ICaseString, SRGBColor> colormap = new HashMap<ICaseString, SRGBColor>();

  static {
    SRGBColorRegistry.colormap.put(new ICaseString("aliceblue"), SRGBColorRegistry.aliceblue);
    SRGBColorRegistry.colormap.put(new ICaseString("antiquewhite"), SRGBColorRegistry.antiquewhite);
    SRGBColorRegistry.colormap.put(new ICaseString("aqua"), SRGBColorRegistry.aqua);
    SRGBColorRegistry.colormap.put(new ICaseString("aquamarine"), SRGBColorRegistry.aquamarine);
    SRGBColorRegistry.colormap.put(new ICaseString("azure"), SRGBColorRegistry.azure);
    SRGBColorRegistry.colormap.put(new ICaseString("beige"), SRGBColorRegistry.beige);
    SRGBColorRegistry.colormap.put(new ICaseString("bisque"), SRGBColorRegistry.bisque);
    SRGBColorRegistry.colormap.put(new ICaseString("black"), SRGBColorRegistry.black);
    SRGBColorRegistry.colormap.put(new ICaseString("blanchedalmond"),
        SRGBColorRegistry.blanchedalmond);
    SRGBColorRegistry.colormap.put(new ICaseString("blue"), SRGBColorRegistry.blue);
    SRGBColorRegistry.colormap.put(new ICaseString("blueviolet"), SRGBColorRegistry.blueviolet);
    SRGBColorRegistry.colormap.put(new ICaseString("brown"), SRGBColorRegistry.brown);
    SRGBColorRegistry.colormap.put(new ICaseString("burlywood"), SRGBColorRegistry.burlywood);
    SRGBColorRegistry.colormap.put(new ICaseString("cadetblue"), SRGBColorRegistry.cadetblue);
    SRGBColorRegistry.colormap.put(new ICaseString("chartreuse"), SRGBColorRegistry.chartreuse);
    SRGBColorRegistry.colormap.put(new ICaseString("chocolate"), SRGBColorRegistry.chocolate);
    SRGBColorRegistry.colormap.put(new ICaseString("coral"), SRGBColorRegistry.coral);
    SRGBColorRegistry.colormap.put(new ICaseString("cornflowerblue"),
        SRGBColorRegistry.cornflowerblue);
    SRGBColorRegistry.colormap.put(new ICaseString("cornsilk"), SRGBColorRegistry.cornsilk);
    SRGBColorRegistry.colormap.put(new ICaseString("crimson"), SRGBColorRegistry.crimson);
    SRGBColorRegistry.colormap.put(new ICaseString("cyan"), SRGBColorRegistry.cyan);
    SRGBColorRegistry.colormap.put(new ICaseString("darkblue"), SRGBColorRegistry.darkblue);
    SRGBColorRegistry.colormap.put(new ICaseString("darkcyan"), SRGBColorRegistry.darkcyan);
    SRGBColorRegistry.colormap.put(new ICaseString("darkgoldenrod"),
        SRGBColorRegistry.darkgoldenrod);
    SRGBColorRegistry.colormap.put(new ICaseString("darkgray"), SRGBColorRegistry.darkgray);
    SRGBColorRegistry.colormap.put(new ICaseString("darkgreen"), SRGBColorRegistry.darkgreen);
    SRGBColorRegistry.colormap.put(new ICaseString("darkgrey"), SRGBColorRegistry.darkgrey);
    SRGBColorRegistry.colormap.put(new ICaseString("darkkhaki"), SRGBColorRegistry.darkkhaki);
    SRGBColorRegistry.colormap.put(new ICaseString("darkmagenta"), SRGBColorRegistry.darkmagenta);
    SRGBColorRegistry.colormap.put(new ICaseString("darkolivegreen"),
        SRGBColorRegistry.darkolivegreen);
    SRGBColorRegistry.colormap.put(new ICaseString("darkorange"), SRGBColorRegistry.darkorange);
    SRGBColorRegistry.colormap.put(new ICaseString("darkorchid"), SRGBColorRegistry.darkorchid);
    SRGBColorRegistry.colormap.put(new ICaseString("darkred"), SRGBColorRegistry.darkred);
    SRGBColorRegistry.colormap.put(new ICaseString("darksalmon"), SRGBColorRegistry.darksalmon);
    SRGBColorRegistry.colormap.put(new ICaseString("darkseagreen"), SRGBColorRegistry.darkseagreen);
    SRGBColorRegistry.colormap.put(new ICaseString("darkslateblue"),
        SRGBColorRegistry.darkslateblue);
    SRGBColorRegistry.colormap.put(new ICaseString("darkslategray"),
        SRGBColorRegistry.darkslategray);
    SRGBColorRegistry.colormap.put(new ICaseString("darkslategrey"),
        SRGBColorRegistry.darkslategrey);
    SRGBColorRegistry.colormap.put(new ICaseString("darkturquoise"),
        SRGBColorRegistry.darkturquoise);
    SRGBColorRegistry.colormap.put(new ICaseString("darkviolet"), SRGBColorRegistry.darkviolet);
    SRGBColorRegistry.colormap.put(new ICaseString("deeppink"), SRGBColorRegistry.deeppink);
    SRGBColorRegistry.colormap.put(new ICaseString("deepskyblue"), SRGBColorRegistry.deepskyblue);
    SRGBColorRegistry.colormap.put(new ICaseString("dimgray"), SRGBColorRegistry.dimgray);
    SRGBColorRegistry.colormap.put(new ICaseString("dimgrey"), SRGBColorRegistry.dimgrey);
    SRGBColorRegistry.colormap.put(new ICaseString("dodgerblue"), SRGBColorRegistry.dodgerblue);
    SRGBColorRegistry.colormap.put(new ICaseString("firebrick"), SRGBColorRegistry.firebrick);
    SRGBColorRegistry.colormap.put(new ICaseString("floralwhite"), SRGBColorRegistry.floralwhite);
    SRGBColorRegistry.colormap.put(new ICaseString("forestgreen"), SRGBColorRegistry.forestgreen);
    SRGBColorRegistry.colormap.put(new ICaseString("fuchsia"), SRGBColorRegistry.fuchsia);
    SRGBColorRegistry.colormap.put(new ICaseString("gainsboro"), SRGBColorRegistry.gainsboro);
    SRGBColorRegistry.colormap.put(new ICaseString("ghostwhite"), SRGBColorRegistry.ghostwhite);
    SRGBColorRegistry.colormap.put(new ICaseString("gold"), SRGBColorRegistry.gold);
    SRGBColorRegistry.colormap.put(new ICaseString("goldenrod"), SRGBColorRegistry.goldenrod);
    SRGBColorRegistry.colormap.put(new ICaseString("gray"), SRGBColorRegistry.gray);
    SRGBColorRegistry.colormap.put(new ICaseString("green"), SRGBColorRegistry.green);
    SRGBColorRegistry.colormap.put(new ICaseString("greenyellow"), SRGBColorRegistry.greenyellow);
    SRGBColorRegistry.colormap.put(new ICaseString("grey"), SRGBColorRegistry.grey);
    SRGBColorRegistry.colormap.put(new ICaseString("honeydew"), SRGBColorRegistry.honeydew);
    SRGBColorRegistry.colormap.put(new ICaseString("hotpink"), SRGBColorRegistry.hotpink);
    SRGBColorRegistry.colormap.put(new ICaseString("indianred"), SRGBColorRegistry.indianred);
    SRGBColorRegistry.colormap.put(new ICaseString("indigo"), SRGBColorRegistry.indigo);
    SRGBColorRegistry.colormap.put(new ICaseString("ivory"), SRGBColorRegistry.ivory);
    SRGBColorRegistry.colormap.put(new ICaseString("khaki"), SRGBColorRegistry.khaki);
    SRGBColorRegistry.colormap.put(new ICaseString("lavender"), SRGBColorRegistry.lavender);
    SRGBColorRegistry.colormap.put(new ICaseString("lavenderblush"),
        SRGBColorRegistry.lavenderblush);
    SRGBColorRegistry.colormap.put(new ICaseString("lawngreen"), SRGBColorRegistry.lawngreen);
    SRGBColorRegistry.colormap.put(new ICaseString("lemonchiffon"), SRGBColorRegistry.lemonchiffon);
    SRGBColorRegistry.colormap.put(new ICaseString("lightblue"), SRGBColorRegistry.lightblue);
    SRGBColorRegistry.colormap.put(new ICaseString("lightcoral"), SRGBColorRegistry.lightcoral);
    SRGBColorRegistry.colormap.put(new ICaseString("lightcyan"), SRGBColorRegistry.lightcyan);
    SRGBColorRegistry.colormap.put(new ICaseString("lightgoldenrodyellow"),
        SRGBColorRegistry.lightgoldenrodyellow);
    SRGBColorRegistry.colormap.put(new ICaseString("lightgray"), SRGBColorRegistry.lightgray);
    SRGBColorRegistry.colormap.put(new ICaseString("lightgreen"), SRGBColorRegistry.lightgreen);
    SRGBColorRegistry.colormap.put(new ICaseString("lightgrey"), SRGBColorRegistry.lightgrey);
    SRGBColorRegistry.colormap.put(new ICaseString("lightpink"), SRGBColorRegistry.lightpink);
    SRGBColorRegistry.colormap.put(new ICaseString("lightsalmon"), SRGBColorRegistry.lightsalmon);
    SRGBColorRegistry.colormap.put(new ICaseString("lightseagreen"),
        SRGBColorRegistry.lightseagreen);
    SRGBColorRegistry.colormap.put(new ICaseString("lightskyblue"), SRGBColorRegistry.lightskyblue);
    SRGBColorRegistry.colormap.put(new ICaseString("lightslategray"),
        SRGBColorRegistry.lightslategray);
    SRGBColorRegistry.colormap.put(new ICaseString("lightslategrey"),
        SRGBColorRegistry.lightslategrey);
    SRGBColorRegistry.colormap.put(new ICaseString("lightsteelblue"),
        SRGBColorRegistry.lightsteelblue);
    SRGBColorRegistry.colormap.put(new ICaseString("lightyellow"), SRGBColorRegistry.lightyellow);
    SRGBColorRegistry.colormap.put(new ICaseString("lime"), SRGBColorRegistry.lime);
    SRGBColorRegistry.colormap.put(new ICaseString("limegreen"), SRGBColorRegistry.limegreen);
    SRGBColorRegistry.colormap.put(new ICaseString("linen"), SRGBColorRegistry.linen);
    SRGBColorRegistry.colormap.put(new ICaseString("magenta"), SRGBColorRegistry.magenta);
    SRGBColorRegistry.colormap.put(new ICaseString("maroon"), SRGBColorRegistry.maroon);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumaquamarine"),
        SRGBColorRegistry.mediumaquamarine);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumblue"), SRGBColorRegistry.mediumblue);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumorchid"), SRGBColorRegistry.mediumorchid);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumpurple"), SRGBColorRegistry.mediumpurple);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumseagreen"),
        SRGBColorRegistry.mediumseagreen);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumslateblue"),
        SRGBColorRegistry.mediumslateblue);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumspringgreen"),
        SRGBColorRegistry.mediumspringgreen);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumturquoise"),
        SRGBColorRegistry.mediumturquoise);
    SRGBColorRegistry.colormap.put(new ICaseString("mediumvioletred"),
        SRGBColorRegistry.mediumvioletred);
    SRGBColorRegistry.colormap.put(new ICaseString("midnightblue"), SRGBColorRegistry.midnightblue);
    SRGBColorRegistry.colormap.put(new ICaseString("mintcream"), SRGBColorRegistry.mintcream);
    SRGBColorRegistry.colormap.put(new ICaseString("mistyrose"), SRGBColorRegistry.mistyrose);
    SRGBColorRegistry.colormap.put(new ICaseString("moccasin"), SRGBColorRegistry.moccasin);
    SRGBColorRegistry.colormap.put(new ICaseString("navajowhite"), SRGBColorRegistry.navajowhite);
    SRGBColorRegistry.colormap.put(new ICaseString("navy"), SRGBColorRegistry.navy);
    SRGBColorRegistry.colormap.put(new ICaseString("oldlace"), SRGBColorRegistry.oldlace);
    SRGBColorRegistry.colormap.put(new ICaseString("olive"), SRGBColorRegistry.olive);
    SRGBColorRegistry.colormap.put(new ICaseString("olivedrab"), SRGBColorRegistry.olivedrab);
    SRGBColorRegistry.colormap.put(new ICaseString("orange"), SRGBColorRegistry.orange);
    SRGBColorRegistry.colormap.put(new ICaseString("orangered"), SRGBColorRegistry.orangered);
    SRGBColorRegistry.colormap.put(new ICaseString("orchid"), SRGBColorRegistry.orchid);
    SRGBColorRegistry.colormap.put(new ICaseString("palegoldenrod"),
        SRGBColorRegistry.palegoldenrod);
    SRGBColorRegistry.colormap.put(new ICaseString("palegreen"), SRGBColorRegistry.palegreen);
    SRGBColorRegistry.colormap.put(new ICaseString("paleturquoise"),
        SRGBColorRegistry.paleturquoise);
    SRGBColorRegistry.colormap.put(new ICaseString("palevioletred"),
        SRGBColorRegistry.palevioletred);
    SRGBColorRegistry.colormap.put(new ICaseString("papayawhip"), SRGBColorRegistry.papayawhip);
    SRGBColorRegistry.colormap.put(new ICaseString("peachpuff"), SRGBColorRegistry.peachpuff);
    SRGBColorRegistry.colormap.put(new ICaseString("peru"), SRGBColorRegistry.peru);
    SRGBColorRegistry.colormap.put(new ICaseString("pink"), SRGBColorRegistry.pink);
    SRGBColorRegistry.colormap.put(new ICaseString("plum"), SRGBColorRegistry.plum);
    SRGBColorRegistry.colormap.put(new ICaseString("powderblue"), SRGBColorRegistry.powderblue);
    SRGBColorRegistry.colormap.put(new ICaseString("purple"), SRGBColorRegistry.purple);
    SRGBColorRegistry.colormap.put(new ICaseString("red"), SRGBColorRegistry.red);
    SRGBColorRegistry.colormap.put(new ICaseString("rosybrown"), SRGBColorRegistry.rosybrown);
    SRGBColorRegistry.colormap.put(new ICaseString("royalblue"), SRGBColorRegistry.royalblue);
    SRGBColorRegistry.colormap.put(new ICaseString("saddlebrown"), SRGBColorRegistry.saddlebrown);
    SRGBColorRegistry.colormap.put(new ICaseString("salmon"), SRGBColorRegistry.salmon);
    SRGBColorRegistry.colormap.put(new ICaseString("sandybrown"), SRGBColorRegistry.sandybrown);
    SRGBColorRegistry.colormap.put(new ICaseString("seagreen"), SRGBColorRegistry.seagreen);
    SRGBColorRegistry.colormap.put(new ICaseString("seashell"), SRGBColorRegistry.seashell);
    SRGBColorRegistry.colormap.put(new ICaseString("sienna"), SRGBColorRegistry.sienna);
    SRGBColorRegistry.colormap.put(new ICaseString("silver"), SRGBColorRegistry.silver);
    SRGBColorRegistry.colormap.put(new ICaseString("skyblue"), SRGBColorRegistry.skyblue);
    SRGBColorRegistry.colormap.put(new ICaseString("slateblue"), SRGBColorRegistry.slateblue);
    SRGBColorRegistry.colormap.put(new ICaseString("slategray"), SRGBColorRegistry.slategray);
    SRGBColorRegistry.colormap.put(new ICaseString("slategrey"), SRGBColorRegistry.slategrey);
    SRGBColorRegistry.colormap.put(new ICaseString("snow"), SRGBColorRegistry.snow);
    SRGBColorRegistry.colormap.put(new ICaseString("springgreen"), SRGBColorRegistry.springgreen);
    SRGBColorRegistry.colormap.put(new ICaseString("steelblue"), SRGBColorRegistry.steelblue);
    SRGBColorRegistry.colormap.put(new ICaseString("tan"), SRGBColorRegistry.tan);
    SRGBColorRegistry.colormap.put(new ICaseString("teal"), SRGBColorRegistry.teal);
    SRGBColorRegistry.colormap.put(new ICaseString("thistle"), SRGBColorRegistry.thistle);
    SRGBColorRegistry.colormap.put(new ICaseString("tomato"), SRGBColorRegistry.tomato);
    SRGBColorRegistry.colormap.put(new ICaseString("turquoise"), SRGBColorRegistry.turquoise);
    SRGBColorRegistry.colormap.put(new ICaseString("violet"), SRGBColorRegistry.violet);
    SRGBColorRegistry.colormap.put(new ICaseString("wheat"), SRGBColorRegistry.wheat);
    SRGBColorRegistry.colormap.put(new ICaseString("white"), SRGBColorRegistry.white);
    SRGBColorRegistry.colormap.put(new ICaseString("whitesmoke"), SRGBColorRegistry.whitesmoke);
    SRGBColorRegistry.colormap.put(new ICaseString("yellow"), SRGBColorRegistry.yellow);
    SRGBColorRegistry.colormap.put(new ICaseString("yellowgreen"), SRGBColorRegistry.yellowgreen);
  }

  /**
   * Gets the color by its name.
   * 
   * @param name the name
   * @return the color or null if not found
   */
  public static SRGBColor getColorByName(final ICaseString name) {
    final SRGBColor c = SRGBColorRegistry.colormap.get(name);
    if (c == null) {
      return null;
    } else {
      return c;
    }
  }

  /**
   * Instantiates a new rgb color registry.
   */
  private SRGBColorRegistry() {
    // nothing to do
  }
}
