package main.java.utils;

/**
 * Created by Santi on 7/27/2017.
 */
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * {@link DragResizerXY} can be used to add mouse listeners to a {@link Region}
 * and make it resizable by the user by clicking and dragging the border in the
 * same way as a window.
 * <p>
 * Height and Width resizing is working (hopefully) properly
 *
 * <pre>
 * DragResizer.makeResizable(myAnchorPane);
 * </pre>
 *
 * @author Cannibalsticky (modified from the original DragResizer created by AndyTill)
 *
 */
public class DragResizerXY { //TODO Fix the hackiness for dragging from left side
                             //TODO Add a static enum class to replace booleans for ease of access
    /**
     * The margin around the control that a user can click in to start resizing
     * the region.
     */
    private static final int RESIZE_MARGIN = 20;

    private final Region region;

    private double y;

    private double x;

    private boolean initMinHeight;

    private boolean initMinWidth;

    private boolean draggableZoneX, draggableZoneY;

    private boolean dragging;

    private DragResizerXY(Region aRegion) {
        region = aRegion;
    }

    public static void makeResizable(Region region, boolean oppositeSide) {
        final DragResizerXY resizer = new DragResizerXY(region);

        region.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mousePressed(event, oppositeSide);
            }
        });
        region.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseDragged(event, oppositeSide);
            }
        });
        region.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseOver(event, oppositeSide);
            }
        });
        region.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resizer.mouseReleased(event);
            }
        });
    }

    protected void mouseReleased(MouseEvent event) {
        dragging = false;
        region.setCursor(Cursor.DEFAULT);
    }

    protected void mouseOver(MouseEvent event, boolean oppositeSide) {
        if (isInDraggableZone(event, oppositeSide) || dragging) {
            if (draggableZoneY) {
                region.setCursor(Cursor.S_RESIZE);
            }

            if (draggableZoneX) {
                region.setCursor(Cursor.E_RESIZE);
            }

        } else {
            region.setCursor(Cursor.DEFAULT);
        }
    }


    //had to use 2 variables for the controll, tried without, had unexpected behaviour (going big was ok, going small nope.)
    protected boolean isInDraggableZone(MouseEvent event, boolean oppositeSide) {
        double marginHeight = oppositeSide ? 0 : region.getHeight();
        double marginWidth = oppositeSide ? 0 : region.getWidth();

        if (!oppositeSide) {
            draggableZoneY = (boolean) (event.getY() > (marginHeight - RESIZE_MARGIN));
            draggableZoneX = (boolean) (event.getX() > (marginWidth - RESIZE_MARGIN));
        } else {
            draggableZoneY = (boolean) (event.getY() < (marginHeight + RESIZE_MARGIN));
            draggableZoneX = (boolean) (event.getX() < (marginWidth + RESIZE_MARGIN));
        }


        return (draggableZoneY || draggableZoneX);
    }

    protected void mouseDragged(MouseEvent event, boolean oppositeSide) {
        if (!dragging) {
            return;
        }

        if (draggableZoneY) {
            double mousey = oppositeSide ? event.getY() * -1 : event.getY();

            double newHeight = region.getMinHeight() + (mousey - y);

            region.setMinHeight(newHeight);

            y = mousey;
        }

        if (draggableZoneX) {
            double mousex = oppositeSide ? event.getX() * -1 : event.getX();

            double newWidth = region.getMinWidth() + (mousex - x);

            //For debugging
            System.out.println("----------------------------------\n");
            System.out.println("Min width: " + region.getMinWidth());
            System.out.println("Mousex: " + mousex);
            System.out.println("x: " + x);
            System.out.println("-----------------------------------\n\n");
            region.setMinWidth(newWidth);

            x = mousex;

        }

    }

    protected void mousePressed(MouseEvent event, boolean oppositeSide) {

        // ignore clicks outside of the draggable margin
        if (!isInDraggableZone(event, oppositeSide)) {
            return;
        }

        dragging = true;

        // make sure that the minimum height is set to the current height once,
        // setting a min height that is smaller than the current height will
        // have no effect
        if (!initMinHeight) {
            region.setMinHeight(region.getHeight());
            initMinHeight = true;
        }

        y = oppositeSide ? event.getY() * -1 : event.getY();

        if (!initMinWidth) {
            region.setMinWidth(region.getWidth());
            initMinWidth = true;
        }

        x = oppositeSide ? event.getX() * -1 : event.getX();
    }
}