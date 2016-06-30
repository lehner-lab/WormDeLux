package org.kuleuven.lineagetree;

public class NucleusUtilities {

	public static int getPixelCountInNucleus(Nucleus n, float xyResolution,
			float zResolution) {
		int count = 0;
		double rSquared = n.getRadius() * n.getRadius();

		double firstz = (Math.ceil((n.getZ() - n.getRadius()) / zResolution));
		double lastz = (Math.floor((n.getZ() + n.getRadius()) / zResolution));

		for (double z = firstz; z <= lastz; z++) {
			double currentDeltaZ = n.getZ() - z * zResolution;
			double localrSquared = rSquared - currentDeltaZ * currentDeltaZ;
			double localR = Math.sqrt(localrSquared);
			double firstx = (Math.ceil((n.getX() - localR) / xyResolution));
			double lastx = (Math.floor((n.getX() + localR) / xyResolution));

			for (double x = firstx; x <= lastx; x++) {
				double currentDeltaX = n.getX() - x * xyResolution;
				double widthy = Math.sqrt(localrSquared - currentDeltaX
						* currentDeltaX);
				double firsty = (Math.ceil((n.getY() - widthy) / xyResolution));
				int valuey = (int) (Math.floor((n.getY() + widthy)
						/ xyResolution) - firsty) + 1;
			//	System.out.println(valuey);
				count += valuey;
			}
		}
		return count;
	}

	public static int getPixelCountSharedBetweenNuclei(Nucleus n1, Nucleus n2,
			float xyResolution, float zResolution) {
		int count = 0;
		double rSquared1 = n1.getRadius() * n1.getRadius();
		double rSquared2 = n2.getRadius() * n2.getRadius();
		if(n1.doesTouch(n2)){
			double firstz = (Math.ceil(Math.max((n1.getZ() - n1.getRadius()),(n2.getZ() - n2.getRadius())) / zResolution));
			double lastz = (Math.floor(Math.min((n1.getZ() + n1.getRadius()),(n2.getZ()+n2.getRadius())) / zResolution));
			for (double z = firstz; z <= lastz; z++) {
				double currentDeltaZ1 = n1.getZ() - z * zResolution;
				double localrSquared1 = rSquared1 - currentDeltaZ1 * currentDeltaZ1;
				double localR1 = Math.sqrt(localrSquared1);
				double currentDeltaZ2 = n2.getZ() - z * zResolution;
				double localrSquared2 = rSquared2 - currentDeltaZ2 * currentDeltaZ2;
				double localR2 = Math.sqrt(localrSquared2);
				
				double firstx = (Math.ceil(Math.max((n1.getX() - localR1),(n2.getX()-localR2)) / xyResolution));
				double lastx = (Math.floor(Math.min((n1.getX() + localR1),(n2.getX()+localR2)) / xyResolution));
				
				for (double x = firstx; x <= lastx; x++) {
					double currentDeltaX1 = n1.getX() - x * xyResolution;
					double widthY1 = Math.sqrt(localrSquared1 - currentDeltaX1
							* currentDeltaX1);
					double currentDeltaX2 = n2.getX() - x * xyResolution;
					double widthY2 = Math.sqrt(localrSquared2 - currentDeltaX2
							* currentDeltaX2);
					double firstY1 = (Math.ceil(Math.max((n1.getY() - widthY1),(n2.getY()-widthY2)) / xyResolution));
					int valuey = (int) (Math.floor(Math.min((n1.getY() + widthY1),(n2.getY()+widthY2)) / xyResolution) - firstY1) + 1;
				//	System.out.println(valuey);
					count += valuey;
				}
			}	
		}
		return count;
	}
	public static float getDiceForOverlapNuclei(Nucleus n1, Nucleus n2,
			float xyResolution, float zResolution) {
		int shared = getPixelCountSharedBetweenNuclei(n1, n2, xyResolution, zResolution);
		if(shared>0){
			int n1Count = getPixelCountInNucleus(n1, xyResolution, zResolution);	
			int n2Count = getPixelCountInNucleus(n2, xyResolution, zResolution);
			return (2f*shared)/(n1Count+n2Count);
		}
		return 0f;
	}
}
