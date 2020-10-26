import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class ColorTools {

    public static double[] RGBtoXYZ(int rgb) {
      //sR, sG and sB (Standard RGB) input range = 0 ÷ 255
      //X, Y and Z output refer to a D65/2° standard illuminant.

      double var_R = ( ((double)rgb) / 255 );
      double var_G = ( ((double)rgb) / 255 );
      double var_B = ( ((double)rgb) / 255 );

      if(var_R > 0.04045) {
          var_R = Math.pow((( var_R + 0.055) / 1.055 ), 2.4);
      } 
      else {
          var_R = var_R / 12.92;
      }
      
      if (var_G > 0.04045) {
          var_G = Math.pow((( var_G + 0.055) / 1.055 ), 2.4);
      } 
      else {
          var_G = var_G / 12.92;
      }
      
      if ( var_B > 0.04045 ) {
          var_B = Math.pow((( var_B + 0.055) / 1.055 ), 2.4);
      } 
      else {
          var_B = var_B / 12.92;
      }

      var_R = var_R * 100;
      var_G = var_G * 100;
      var_B = var_B * 100;

      double [] XYZ = new double [3];

      XYZ[0] = var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805;
      XYZ[1] = var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722;
      XYZ[2] = var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505;
        
      
      return XYZ;
    }
    
    public static double[] XYZtoCIELAB(double[] XYZ) {
        int ReferenceX = 100;
        int ReferenceY = 100;
        int ReferenceZ = 100;

        
        double var_X = XYZ[0] / ReferenceX;
        double var_Y = XYZ[1] / ReferenceY;
        double var_Z = XYZ[2] / ReferenceZ;

        if (var_X > 0.008856 ) {
            var_X = Math.pow(var_X ,1/3.0);
        }
        else {
            var_X = ( 7.787 * var_X ) + ( 16 / 116.0 );
        }
                
        if (var_Y > 0.008856) {
            var_Y = Math.pow(var_Y ,1/3.0);
        }
        else {
            var_Y = ( 7.787 * var_Y ) + ( 16 / 116.0 );
        }
                
        if ( var_Z > 0.008856 ) {
            var_Z = Math.pow(var_Z ,1/3.0);
        }
        else{
            var_Z = ( 7.787 * var_Z ) + ( 16 / 116.0 );
        }
        
        double [] LAB = new double [3];


        LAB[0] = ( 116 * var_Y ) - 16;
        LAB[1] = 500 * ( var_X - var_Y );
        LAB[2] = 200 * ( var_Y - var_Z );
        
        return LAB;
    }
    
    public static double[] RBGtoCIELAB(int rgb) {
        return XYZtoCIELAB(RGBtoXYZ(rgb));
    }
    
    public static double[][] RBGtoCIELAB(int RGBs []) {
        double[][] LABs = new double [RGBs.length][3];
        for(int i = 0; i < RGBs.length; ++i) {
            LABs[i] = XYZtoCIELAB(RGBtoXYZ(RGBs[i]));
        }
        return LABs;
    }
    
    public static double distCIELAB(double[] LAB_1, double[] LAB_2) {
        double dist = 0;
        for(int i = 0; i < 3; ++i) {
            dist += (LAB_1[i]- LAB_2[i]) * (LAB_1[i]- LAB_2[i]);
        }
        return dist;
    }
    
    public static int findClosestCIELAB(double[][] LABs, double[] elem) {
        double minDist = distCIELAB(elem, LABs[0]);
        
        int closestIndex = 0;
        
        for(int i = 0; i < LABs.length; ++i) {
            double currDist = distCIELAB(elem, LABs[i]);
            if(minDist > currDist) {
                minDist = currDist;
                closestIndex = i;
            }
        }
        
        return closestIndex;
    }
    
    public static int distRGB(int rgb1, int rgb2) {
        Color c1 = new Color(rgb1);
        Color c2 = new Color(rgb2);
       
        int distR = c1.getRed() > c2.getRed() ? c1.getRed() - c2.getRed() : c2.getRed() - c1.getRed();
        int distG = c1.getGreen() > c2.getGreen() ? c1.getGreen() - c2.getGreen() : c2.getGreen() - c1.getGreen();
        int distB = c1.getBlue() > c2.getBlue() ? c1.getBlue() - c2.getBlue() : c2.getBlue() - c1.getBlue();

        
        
        return distR + distG + distB;
    }
    
    public static int findClosestRGB(int means[], int elem){
        int minDist = distRGB(elem, means[0]);
        
        int closestIndex = 0;
        
        for(int i = 0; i < means.length; ++i) {
            int currDist = distRGB(elem, means[i]);
            if(minDist > currDist) {
                minDist = currDist;
                closestIndex = i;
            }
        }
        
        return closestIndex;
    }
    
    public static int [] findXClosestsRGB(int means[], int elem){
        int minDist = distRGB(elem, means[0]);
        
        int closestIndex = 0;
        
        //int [] dists = new int [means.length];
        
        List <Integer> equals = new ArrayList<Integer>();
       
        for(int i = 0; i < means.length; ++i) {
            int currDist = distRGB(elem, means[i]);
            //dists[i] = currDist;
            if(minDist > currDist) {
                minDist = currDist;
                closestIndex = i;
                
                equals.clear();
                equals.add(i);
            }else if(currDist == minDist) {
                equals.add(i);
            }
        }
        
        int [] closets = new int [equals.size()];

         for(int i = 0; i < closets.length; ++i) {
             closets[i] = equals.get(i);
         }
            
        return closets;
    }
    
    public static int findClosestMonochrome(int means[], int elem){
        int minDist = elem > means[0] ? elem - means[0] : means[0] - elem;
        
        int closestIndex = 0;
        
        for(int i = 0; i < means.length; ++i) {
            int currDist = elem > means[i] ? elem - means[i] : means[i] - elem;
            if(minDist > currDist) {
                minDist = currDist;
                closestIndex = i;
            }
        }
        
        return closestIndex;
    }

}
