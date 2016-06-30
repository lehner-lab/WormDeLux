/*
 *      ColorConstants.java 1.0 98/11/23
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package org.kuleuven.lineager;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Color3f;

public class ColorConstants {
	public static final Color3f green;
	public static final Color3f red;
	public static final Color3f blue;
	public static final Color3f yellow;
	public static final Color3f cyan;
	public static final Color3f magenta;
	public static final Color3f white;
	public static final Color3f black;
	public static final Color3f gray;
	public static final Color3f pink;
	public static final Color3f orange;
	private static Color3f lsBlue;
	private static Color3f aguamarine;
	private static Color3f brown;
	static {
		red = new Color3f(1.0f, 0.0f, 0.0f);
		green = new Color3f(0.0f, 1.0f, 0.0f);
		blue = new Color3f(0.0f, 0.0f, 1.0f);
		yellow = new Color3f(1.0f, 1.0f, 0.0f);
		cyan = new Color3f(0.0f, 1.0f, 1.0f);
		magenta = new Color3f(1.0f, 0.0f, 1.0f);
		white = new Color3f(1.0f, 1.0f, 1.0f);
		black = new Color3f(0.0f, 0.0f, 0.0f);
		gray = new Color3f(((float) Color.gray.getRed()) / 256,
				((float) Color.gray.getGreen()) / 256, ((float) Color.gray.getBlue()) / 256);
		pink = new Color3f(((float) Color.pink.getRed()) / 256,
				((float) Color.pink.getGreen()) / 256, ((float) Color.pink.getBlue()) / 256);
		orange = new Color3f(((float) Color.orange.getRed()) / 256,
				((float) Color.orange.getGreen()) / 256, ((float) Color.orange.getBlue()) / 256);
		lsBlue = new Color3f(132f / 255, 112f / 255, 1f);
		aguamarine = new Color3f(69f / 255, 139f / 255, 116f / 255);
		brown = new Color3f(139f / 255, 69f / 255, 19f / 255);
		Map<Color3f, String> namesT = new HashMap<Color3f, String>();
		namesT.put(red, "red");
		namesT.put(green, "green");
		namesT.put(blue, "blue");
		namesT.put(yellow, "yellow");
		namesT.put(cyan, "cyan");
		namesT.put(magenta, "magenta");
		namesT.put(white, "white");
		namesT.put(black, "black");
		namesT.put(gray, "gray");
		namesT.put(pink, "pink");
		namesT.put(orange, "orange");
		namesT.put(lsBlue, "lsBlue");
		namesT.put(aguamarine, "aguamarine");
		namesT.put(brown, "brown");
		color2name = namesT;
		Map<String, Color3f> colorsT = new HashMap<String, Color3f>();
		for (Entry<Color3f, String> e : namesT.entrySet()) {
			colorsT.put(e.getValue(), e.getKey());
		}
		name2Color = colorsT;

	}
	public static final Map<Color3f, String> color2name;
	public static final Map<String, Color3f> name2Color;
}
