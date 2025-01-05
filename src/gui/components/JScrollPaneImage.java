package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class JScrollPaneImage extends JScrollPane {
    private BufferedImage image;
    private Canvas canvas;

    public JScrollPaneImage(BufferedImage image) {
        setViewportView(canvas = new Canvas());
        setImage(image);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        if (this.image == null) {
            canvas.setSize(image.getWidth(), image.getHeight());
            canvas.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        }
        this.image = image;
        canvas.validate();
        canvas.repaint();
    }

    public class Canvas extends JComponent implements MouseWheelListener, MouseMotionListener, MouseListener {
        private double zoom = 1.0;
        public static final double SCALE_STEP = 0.1d;
        private Point origin = new Point();
        private double previousZoom = zoom;
        AffineTransform tx = new AffineTransform();
        private double scrollX = 0d;
        private double scrollY = 0d;

        public Canvas() {
            addMouseWheelListener(this);
            addMouseMotionListener(this);
            addMouseListener(this);
            setAutoscrolls(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.clearRect(0, 0, getWidth(), getHeight());
            g2d.transform(tx);
            int dx = 0, dy = 0;
            if (getWidth() >= getPreferredSize().getWidth() && getHeight() >= getPreferredSize().getHeight()) {
                dx = getWidth() / 2 - getPreferredSize().width / 2;
                dy = getHeight() / 2 - getPreferredSize().height / 2;
            }
            g.drawImage(image, dx, dy, getPreferredSize().width, getPreferredSize().height, null);
            g2d.dispose();
        }

        public void mouseWheelMoved(MouseWheelEvent e) {
            double zoomFactor = -SCALE_STEP * e.getPreciseWheelRotation() * zoom;
            zoom = Math.abs(zoom + zoomFactor);
            //Here we calculate new size of canvas relative to zoom.
            Rectangle realView = getVisibleRect();
            Dimension d = new Dimension(
                    (int) (image.getWidth() * zoom),
                    (int) (image.getHeight() * zoom));
//        if (d.getWidth() >= realView.getWidth() && d.getHeight() >= realView.getHeight()) {
            setPreferredSize(d);
            setSize(d);
            validate();
            followMouseOrCenter(e);
//        }

            //Here we calculate transform for the canvas graphics to scale relative to mouse
            translate(e);
            repaint();
            previousZoom = zoom;
        }

        private void translate(MouseWheelEvent e) {
            Rectangle realView = getVisibleRect();
            Point2D p1 = e == null ? new Point() : e.getPoint();
            Point2D p2;
            try {
                p2 = tx.inverseTransform(p1, null);
            } catch (NoninvertibleTransformException ex) {
                ex.printStackTrace();
                return;
            }
            Dimension d = getSize();
            if (d.getWidth() <= realView.getWidth() && d.getHeight() <= realView.getHeight()) {
                //Zooming and translating relative to the mouse position
                tx.setToIdentity();
                tx.translate(p1.getX(), p1.getY());
                tx.scale(zoom, zoom);
                tx.translate(-p2.getX(), -p2.getY());
            } else {
                //Only zooming, translate is not needed because scrollRectToVisible works;
                tx.setToIdentity();
                tx.scale(zoom, zoom);
            }
        }


        public void followMouseOrCenter(MouseWheelEvent e) {
            Point2D point = e == null ? new Point() : e.getPoint();
            Rectangle visibleRect = getVisibleRect();

            scrollX = point.getX() / previousZoom * zoom - (point.getX() - visibleRect.getX());
            scrollY = point.getY() / previousZoom * zoom - (point.getY() - visibleRect.getY());

            visibleRect.setRect(scrollX, scrollY, visibleRect.getWidth(), visibleRect.getHeight());
            scrollRectToVisible(visibleRect);
        }

        public void mouseDragged(MouseEvent e) {
            if (origin != null) {
                int deltaX = origin.x - e.getX();
                int deltaY = origin.y - e.getY();
                Rectangle view = getVisibleRect();
                Dimension size = getSize();
                view.x += deltaX;
                view.y += deltaY;
                scrollRectToVisible(view);
            }
        }

        public void mouseMoved(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
            origin = new Point(e.getPoint());
        }

        public void mouseReleased(MouseEvent e) {

        }

        public void mouseEntered(MouseEvent e) {

        }

        public void mouseExited(MouseEvent e) {

        }

    }

}
