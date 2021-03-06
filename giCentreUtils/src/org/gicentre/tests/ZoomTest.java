package org.gicentre.tests;

import org.gicentre.utils.move.Ease;
import org.gicentre.utils.move.ZoomPan;
import org.gicentre.utils.move.ZoomPan.ZoomPanDirection;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

//  ****************************************************************************************
/** Tests zooming and panning in a simple Processing sketch. Includes tests for high quality
 *  zoomed text, for constraining zooming and panning, and for zoom-independent display.
 *  @author Jo Wood, giCentre, City University London.
 *  @version 3.4.1, 25th February 2016.
 */ 
//  *****************************************************************************************

/* This file is part of giCentre utilities library. gicentre.utils is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * gicentre.utils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see 
 * http://www.gnu.org/licenses/.
 */
public class ZoomTest extends PApplet
{
    // ------------------------------ Starter method ------------------------------- 

    /** Creates a simple application to test the zooming and panning in Processing.
     *  Can be controlled with the left and right mouse buttons and 'R' to reset 
     *  quickly, and 'B' to do an animated reset with a small bounce.
     *  @param args Command line arguments (ignored). 
     */
    public static void main(String[] args)
    {   
        PApplet.main(new String[] {"org.gicentre.tests.ZoomTest"});
    }

    // ----------------------------- Object variables ------------------------------

    private ZoomPan zoomer;
    private float morphT;           // Controls morphing of the zoom display (0-1).
    private PVector panOffset;      // Pan offset used for morphed panning.
    private double zoomScale;       // Zoom level used for morphed zooming.
    private boolean constrain;		// Determines if zooming and panning is constrained.
    private boolean isMouseMaskOn;	// Determines if the keyboard mouse mask is actived.
    private PVector minBounds,maxBounds;	// Active area beyond which zooming and panning cannot be controlled with mouse.
    
    // ---------------------------- Processing methods -----------------------------

    /** Sets the size and of the sketch and its maximum pixel density.
     */
	public void settings()
	{
		size(800,400);
		pixelDensity(displayDensity());
	}
    
    /** Sets up the sketch.
     */
    public void setup()
    {   
        morphT = 1;                 // 1 indicates no morphing. 
        constrain = false;
        isMouseMaskOn = false;
        zoomer = new ZoomPan(this);
        textFont(createFont("Serif",18),18);
        
        minBounds = new PVector(60,30);
        maxBounds = new PVector(width-25,height-80);
    }

    /** Draws a simple object that can be zoomed and panned.
     */
	public void draw()
    {   
        background(255);
        
        // Draw active area and only allow mouse control within it.
        noStroke();
        fill(250,238,194,180);
        rect(minBounds.x,minBounds.y,maxBounds.x-minBounds.x,maxBounds.y-minBounds.y);
        
        if ((mouseX < minBounds.x) || (mouseX > maxBounds.x) || (mouseY < minBounds.y) || (mouseY > maxBounds.y))
        {
        	zoomer.setMouseMask(-1);
        }
        else
        {
        	if (isMouseMaskOn)
        	{
        		zoomer.setMouseMask(PConstants.SHIFT);
        	}
        	else
        	{
        		zoomer.setMouseMask(0);
        	}
        }
        
        pushMatrix();       // Preserve the non-zoomed display.
        
        if (morphT < 1)
        {
            // Move the zoom pan with an eased transition.
            zoomer.setPanOffset(lerp(panOffset.x,0,Ease.bounceOut(morphT)), 
                                lerp(panOffset.y,0,Ease.bounceOut(morphT)));
            zoomer.setZoomScale(lerp((float)zoomScale,1,Ease.bounceOut(morphT)));
            morphT += 0.02f;
        }
        zoomer.transform();     // Do the zooming and panning.
        
        // Display a simple background
        stroke(120);
        strokeWeight(0.5f);
        for (float x=0; x<=width; x+= width/20f)
        {
            line(x,0,x,height);
        }
        
        for (float y=0; y<=height; y+= height/20f)
        {
            line(0,y,width,y);
        }
        
        // Display some off screen content that only appears when zooming or panning
        strokeWeight(2);
        textSize(8);
        fill(20);
        
        line(-40,-20,-40,-1);
        line(-40,-20,-21,-20);
        textAlign(PConstants.LEFT, PConstants.TOP);
        text("Extent of constrained panning",-40,-20);
        
        line(width+40,-20,width+40,-1);
        line(width+40,-20,width+21,-20);
        textAlign(PConstants.RIGHT, PConstants.TOP);
        text("Extent of constrained panning",width+40,-20);
        
        line(-40,height+20,-40,height+1);
        line(-40,height+20,-21,height+20);
        textAlign(PConstants.LEFT, PConstants.BOTTOM);
        text("Extent of constrained panning",-40,height+20);
        
        textAlign(PConstants.RIGHT, PConstants.BOTTOM);
        text("Extent of constrained panning",width+40,height+20);
        line(width+40,height+20,width+40,height+1);
        line(width+40,height+20,width+21,height+20);
               
        // Display a simple object.
        stroke(80);
        strokeWeight(2);
        fill(180,120,120);
        ellipse(width/2, height/2, 60,60);
        
        fill(20);
        textAlign(PConstants.CENTER, PConstants.CENTER);
        
        // It looks like we no longer need to use Zoomer's improved text method as the Processing bug that 
        // caused irregular text placement under high scaling appears to have been fixed.
        zoomer.text(" Zoom and pan me",width/2,height/2);
        
        text("Zoom and pan me",width/2,height/2);
        
        float fixedSize = (float)Math.sqrt(64/zoomer.getZoomScale());
        textSize(fixedSize);
        text("Text above should be offset by one space",width/2,height/2+8);
        
        
        textAlign(PConstants.LEFT, PConstants.BOTTOM);
        text("Text should be in corner of a grid",8f*width/20,11f*height/20);
        zoomer.text("Text should be in corner of a grid [this version with zoomer.text()]",8f*width/20,11f*height/20);
        textAlign(PConstants.LEFT, PConstants.TOP);
        zoomer.text("If Processing text scaling correct there should be no ghosting or moving of text in the line above",8f*width/20,11f*height/20);
        
        popMatrix();        // Retrieve the non zoomed display
        
        textAlign(PConstants.LEFT, PConstants.BOTTOM);
        fill(0);
        textSize(18);
        text("Zoom scale: "+nfc((float)zoomer.getZoomScale(),4)+
        	 " Pan centre: "+nfc(zoomer.getPanOffset().x,3)+","+nfc(zoomer.getPanOffset().y,3)+
        	 " Constraints are "+(constrain?"on.":"off.")+
        	 " Keyboard mask is "+(isMouseMaskOn?"on.":"off."),10,height-3);
        
        textAlign(RIGHT,TOP);
        text(zoomer.getZoomPanDirection().toString(),width,0);
        
        textAlign(LEFT,TOP);
        String s="";
        if (zoomer.isPanning())
        {
        	s+="panning ";
        }
        if (zoomer.isZooming())
        {
        	s+="zooming ";
        }
        text(s,0,0);
    }
    
    /** Responds to key presses by allowing the display to be reset.
     */
    public void keyPressed()
    {
       
        if ((key == 'b') || (key == 'B'))
        {
            // Resets the zoom/pan with a little bounce.
            panOffset = zoomer.getPanOffset();
            zoomScale = (float)zoomer.getZoomScale();
            morphT = 0;
        }
        else  if ((key == 'c') || (key == 'C'))
        {
            constrain = !constrain;
            
            if (constrain)
            {
            	zoomer.setMaxZoomScale(10);
            	zoomer.setMinZoomScale(1);
            	zoomer.setMaxPanOffset(40, 20);
            }
            else
            {
            	zoomer.setMaxZoomScale(Double.MAX_VALUE);
            	zoomer.setMinZoomScale(Double.MIN_VALUE);
            	zoomer.setMaxPanOffset(-1, -1);
            }
        }
        else  if ((key == 'm') || (key == 'M'))
        {
        	if (isMouseMaskOn)
        	{
        		zoomer.setMouseMask(0);
        		isMouseMaskOn = false;
        	}
        	else
        	{
        		zoomer.setMouseMask(PConstants.SHIFT);
        		isMouseMaskOn = true;
        	}
        }
        
        else  if ((key == 'r') || (key == 'R'))
        {
            zoomer.reset();
        }
        
        else if (key=='d' || key=='D')
        {
        	int ord=zoomer.getZoomPanDirection().ordinal();
        	ord++;
        	if (ord>=ZoomPanDirection.values().length)
        		ord=0;
        	zoomer.setZoomPanDirection(ZoomPanDirection.values()[ord]);
        }
                
    }
}