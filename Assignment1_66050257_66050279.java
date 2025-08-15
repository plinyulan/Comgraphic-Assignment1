import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Assignment1_66050257_66050279 extends JPanel implements ActionListener {
    // CONFIG
    public static final int W = 600, H = 600;
    public static final boolean SHOW_REFERENCE_FRAME = false;
    private final Timer timer;
    private long t0 = System.nanoTime();

    // เวลา <= 5 วินาที
    private static final double TOTAL = 5.0;

    // PALETTE
    static final Color WALL_TOP = new Color(58, 42, 74);
    static final Color WALL_BOT = new Color(42, 33, 58);
    static final Color DOT = new Color(120, 102, 136);
    static final Color WOOD_1 = new Color(66, 44, 48);
    static final Color WOOD_2 = new Color(58, 38, 44);
    static final Color POSTER_BG = new Color(245, 238, 230);
    static final Color POSTER_EDGE = new Color(205, 198, 190);
    static final Color TAPE = new Color(93, 156, 178);
    static final Color SHADOW = new Color(0, 0, 0, 70);
    static final Color BIG_SHADOW = new Color(0, 0, 0, 55);
    static final Color STICK = new Color(170, 160, 150);
    static final Color COCOON = new Color(242, 231, 210);
    static final Color COCOON_LINE = new Color(205, 190, 170);
    static final Color BUTTER_BODY = new Color(100, 70, 60);
    static final Color BUTTER_OUT = new Color(60, 40, 30);

    // ดักแด้ซ้าย/ขวา (ตำแหน่งฐาน)
    private final double Lx = 180, Rx = 420;

    // ขยายดักแด้
    private static final int CO_RX = 44, CO_RY = 64;
    private static final int SHADOW_R = 30;

    // หนอน 2 ตัว
    private final Worm leftWorm = new Worm(new Color(230, 60, 45), new Color(255, 120, 70), 1.05);
    private final Worm rightWorm = new Worm(new Color(250, 210, 40), new Color(255, 165, 50), 1.15);

    // Constructor
    public Assignment1_66050257_66050279() {
        setPreferredSize(new Dimension(W, H));
        setBackground(WALL_BOT);
        timer = new Timer(1000 / 60, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private double secs() {
        return (System.nanoTime() - t0) / 1_000_000_000.0;
    }

    @Override
    protected void paintComponent(Graphics gRaw) {
        super.paintComponent(gRaw);
        Graphics2D g = (Graphics2D) gRaw;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        double T = secs();
        double t = SHOW_REFERENCE_FRAME ? 0.0 : (T % TOTAL);

        // BACKGROUND
        g.setPaint(new GradientPaint(0, 0, WALL_TOP, 0, H, WALL_BOT));
        g.fillRect(0, 0, W, H);

        g.setPaint(DOT);
        for (int y = 70; y <= 280; y += 44)
            for (int x = 70; x <= 530; x += 44)
                g.fill(new Ellipse2D.Double(x - 6, y - 6, 12, 12));

        g.setPaint(WOOD_1);
        g.fillRect(0, 360, W, 240);
        g.setPaint(WOOD_2);
        g.fillRect(0, 520, W, 8);
        g.fillRect(0, 570, W, 6);
        g.setPaint(new Color(255, 255, 255, 25));
        for (int x = 0; x < W; x += 12)
            g.drawLine(x, 410, x + 6, 410);

        g.setPaint(BIG_SHADOW);
        g.fill(new Ellipse2D.Double(120, 480, 360, 110));

        g.setPaint(SHADOW);
        g.fill(makeShadow(165, 175, 300, 95, 18));
        g.fill(makeShadow(435, 195, 300, 95, -18));

        // POSTER
        Shape poster = new RoundRectangle2D.Double(230, 210, 140, 160, 16, 16);
        g.setPaint(POSTER_BG);
        g.fill(poster);
        g.setPaint(POSTER_EDGE);
        g.setStroke(new BasicStroke(2f));
        g.draw(poster);
        g.setPaint(TAPE);
        g.fill(new RoundRectangle2D.Double(242, 200, 22, 18, 5, 5));
        g.fill(new RoundRectangle2D.Double(336, 200, 22, 18, 5, 5));
        drawButterflyOnPoster(g, 300, 292);

        // COCOONS
        double swayL = Math.sin(T * 2.0) * 6, swayR = Math.sin(T * 2.0 + Math.PI * 0.7) * 6;
        double shakeAmp = (t < 1.2 && !SHOW_REFERENCE_FRAME) ? (1.0 - t / 1.2) * 10.0 : 0.0;
        double shakeL = shakeAmp * Math.sin(T * 18.0);
        double shakeR = shakeAmp * Math.sin(T * 19.0 + 0.6);

        double LxNow = Lx + (SHOW_REFERENCE_FRAME ? 0 : (swayL + shakeL));
        double RxNow = Rx + (SHOW_REFERENCE_FRAME ? 0 : (swayR + shakeR));

        double cocoonAlpha = 1.0;
        if (!SHOW_REFERENCE_FRAME && t >= 1.6) {
            cocoonAlpha = 1.0 - (t - 1.6) / (3.4 - 1.6);
            cocoonAlpha = Math.max(0, Math.min(1, cocoonAlpha));
        }

        if (cocoonAlpha > 0 || SHOW_REFERENCE_FRAME) {
            Composite oldC = g.getComposite();
            g.setComposite(AlphaComposite.SrcOver.derive((float) cocoonAlpha));

            g.setPaint(STICK);
            g.setStroke(new BasicStroke(2.8f));
            g.draw(new Line2D.Double(LxNow, 240, LxNow, 360));
            g.draw(new Line2D.Double(RxNow, 240, RxNow, 360));

            drawMidpointEllipseFilled(g, (int) LxNow, 360, CO_RX, CO_RY, COCOON, COCOON_LINE);
            drawMidpointEllipseFilled(g, (int) RxNow, 360, CO_RX, CO_RY, COCOON, COCOON_LINE);
            drawScaledCircleShadow(g, (int) LxNow, 446, SHADOW_R);
            drawScaledCircleShadow(g, (int) RxNow, 446, SHADOW_R);

            g.setComposite(oldC);
        }

        // WORM JUMP
        if (!SHOW_REFERENCE_FRAME) {
            if (t >= 1.6 && t < 3.4) {
                double u = smooth01((t - 1.6) / 1.8);

                double xL = lerp(LxNow + CO_RX * 0.5, W / 2.0 - 55, u);
                double yL = parabola(360 - 20, 360, 130, u);

                double xR = lerp(RxNow - CO_RX * 0.5, W / 2.0 + 55, u);
                double yR = parabola(360 - 20, 360, 130, u);

                double sxAir = 1.0 + 0.06 * Math.sin(u * Math.PI);
                double syAir = 1.0 - 0.06 * Math.sin(u * Math.PI);

                double tiltL = Math.toRadians(-12 + 24 * u);
                double tiltR = Math.toRadians(12 - 24 * u);
                double mouth = 0.65;
                double blink = 0.9;

                leftWorm.draw(g, xL, yL, tiltL, sxAir, syAir, mouth, blink, 1.0);
                rightWorm.draw(g, xR, yR, tiltR, sxAir, syAir, mouth, blink, 1.0);

            } else if (t >= 3.4) {
                double bob = Math.sin(secs() * 3.2) * 8;
                double tilt = Math.toRadians(4) * Math.sin(secs() * 2.0);
                double sx = 1.0 + 0.05 * Math.sin(secs() * 3.4);
                double sy = 1.0 - 0.05 * Math.sin(secs() * 3.4);
                double mouth = 0.5 + 0.5 * Math.sin(secs() * 2.6);
                double blink = Math.max(0.15, 0.85 * Math.abs(Math.sin(secs() * 3.8)));

                leftWorm.draw(g, W / 2.0 - 55, 360 + bob, tilt, sx, sy, mouth, blink, 1.0);
                rightWorm.draw(g, W / 2.0 + 55, 360 + bob, -tilt, sx, sy, mouth, blink, 1.0);
            }
        }
    }

    // Helper functions
    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double smooth01(double x) {
        x = Math.max(0, Math.min(1, x));
        return x * x * (3 - 2 * x);
    }

    private static double parabola(double y0, double y1, double h, double u) {
        return lerp(y0, y1, u) - h * 4 * u * (1 - u);
    }

    private Shape makeShadow(double cx, double cy, double w, double h, double tilt) {
        Path2D p = new Path2D.Double();
        double x0 = cx - w / 2, x1 = cx + w / 2;
        p.moveTo(x0, cy);
        p.quadTo(cx, cy - h, x1, cy);
        p.quadTo(cx, cy + h * 0.45, x0, cy);
        return AffineTransform.getRotateInstance(Math.toRadians(tilt), cx, cy).createTransformedShape(p);
    }

    private void drawButterflyOnPoster(Graphics2D g, double cx, double cy) {
        Shape L = wingShape(-1, cx, cy, 46, 38);
        Shape R = wingShape(1, cx, cy, 46, 38);
        g.setPaint(new GradientPaint((float) (cx - 36), (float) (cy - 10), new Color(245, 140, 90),
                (float) (cx - 5), (float) (cy + 28), new Color(110, 200, 160)));
        g.fill(L);
        g.setPaint(new GradientPaint((float) (cx + 5), (float) (cy + 28), new Color(245, 140, 90),
                (float) (cx + 36), (float) (cy - 10), new Color(110, 200, 230)));
        g.fill(R);
        g.setPaint(BUTTER_OUT);
        g.setStroke(new BasicStroke(2.2f));
        g.draw(L);
        g.draw(R);
        g.setPaint(BUTTER_BODY);
        g.fill(new RoundRectangle2D.Double(cx - 5.5, cy - 18, 11, 36, 8, 8));
    }

    private Shape wingShape(int side, double cx, double cy, double w, double h) {
        double s = side;
        Path2D p = new Path2D.Double();
        p.moveTo(cx, cy);
        p.curveTo(cx + s * w * 0.25, cy - h * 0.9, cx + s * w * 0.90, cy - h * 0.2, cx + s * w * 0.70, cy + h * 0.05);
        p.curveTo(cx + s * w * 0.95, cy + h * 0.60, cx + s * w * 0.20, cy + h * 0.85, cx, cy + h * 0.35);
        p.closePath();
        return p;
    }

    private void drawMidpointEllipseFilled(Graphics2D g, int cx, int cy, int rx, int ry, Color fill, Color fiberLine) {
        List<Point> pts = midpointEllipsePoints(cx, cy, rx, ry);
        Polygon poly = new Polygon();
        for (Point p : pts)
            poly.addPoint(p.x, p.y);
        g.setPaint(fill);
        g.fillPolygon(poly);
        g.setStroke(new BasicStroke(1.4f));
        g.setPaint(fiberLine);
        g.draw(new QuadCurve2D.Double(cx - rx * 0.7, cy - 6, cx, cy - 12, cx + rx * 0.7, cy - 6));
        g.draw(new QuadCurve2D.Double(cx - rx * 0.65, cy + 8, cx, cy + 2, cx + rx * 0.65, cy + 8));
    }

    private List<Point> midpointEllipsePoints(int cx, int cy, int rx, int ry) {
        List<Point> pts = new ArrayList<>();
        int x = 0, y = ry;
        long rx2 = 1L * rx * rx, ry2 = 1L * ry * ry;
        long d1 = ry2 - rx2 * ry + rx2 / 4;
        long dx = 2 * ry2 * x, dy = 2 * rx2 * y;
        while (dx < dy) {
            addSym4(pts, cx, cy, x, y);
            if (d1 < 0) {
                x++;
                dx += 2 * ry2;
                d1 += dx + ry2;
            } else {
                x++;
                y--;
                dx += 2 * ry2;
                dy -= 2 * rx2;
                d1 += dx - dy + ry2;
            }
        }
        long d2 = (long) (ry2 * (x + 0.5) * (x + 0.5) + rx2 * (y - 1) * (y - 1) - rx2 * ry2);
        while (y >= 0) {
            addSym4(pts, cx, cy, x, y);
            if (d2 > 0) {
                y--;
                dy -= 2 * rx2;
                d2 += rx2 - dy;
            } else {
                y--;
                x++;
                dx += 2 * ry2;
                dy -= 2 * rx2;
                d2 += dx - dy + rx2;
            }
        }
        return pts;
    }

    private void addSym4(List<Point> pts, int cx, int cy, int x, int y) {
        pts.add(new Point(cx + x, cy + y));
        pts.add(new Point(cx - x, cy + y));
        pts.add(new Point(cx + x, cy - y));
        pts.add(new Point(cx - x, cy - y));
    }

    private void drawScaledCircleShadow(Graphics2D g, int cx, int cy, int r) {
        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.scale(1.8, 0.72);
        g.setPaint(new Color(0, 0, 0, 55));
        g.fill(new Ellipse2D.Double(-r, -r, r * 2, r * 2));
        g.setTransform(old);
    }

    // Worm
    class Worm {
        private final Color bodyColor;
        private final Color stripeColor;
        private final double baseScale;

        public Worm(Color bodyColor, Color stripeColor, double baseScale) {
            this.bodyColor = bodyColor;
            this.stripeColor = stripeColor;
            this.baseScale = baseScale;
        }

        public void draw(Graphics2D g, double cx, double cy,
                double tiltRad, double stretchX, double stretchY,
                double mouthOpen, double blink, double alpha) {
            Composite oldC = g.getComposite();
            g.setComposite(AlphaComposite.SrcOver.derive((float) Math.max(0, Math.min(1, alpha))));

            AffineTransform old = g.getTransform();
            g.translate(cx, cy);
            g.rotate(tiltRad);
            g.scale(baseScale * stretchX, baseScale * stretchY);

            // เงาใต้ตัว
            drawScaledCircleShadow(g, 0, 120, 22);

            double t = System.nanoTime() / 1_000_000_000.0;

            double headWaveY = 6 * Math.sin(t * 6 + cx * 0.05);
            double headWaveCurve = 10 * Math.sin(t * 5 + cx * 0.1);

            // Body + Head
            Path2D bodyPath = new Path2D.Double();
            bodyPath.moveTo(-30, -80);
            bodyPath.curveTo(-40, -60 + headWaveY, -30, -100 + headWaveY, 0, -100 + headWaveY);
            bodyPath.curveTo(30, -100 + headWaveY, 40 + headWaveCurve, -60 + headWaveY, 30, -80);
            bodyPath.curveTo(50, 40, 50, 40, 18, 95);
            bodyPath.quadTo(0, 110, -18, 95);
            bodyPath.curveTo(-50, 40, -50, 40, -30, -80);
            bodyPath.closePath();

            Area unified = new Area(bodyPath);

            // Gradient และ stroke
            Rectangle2D b = unified.getBounds2D();
            Paint grad = new GradientPaint(
                    (float) b.getCenterX(), (float) (b.getMinY() - 10), bodyColor.brighter(),
                    (float) b.getCenterX(), (float) (b.getMaxY() + 10), bodyColor.darker());
            g.setPaint(grad);
            g.fill(unified);

            g.setPaint(new Color(0, 0, 0, 40));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(unified);

            g.setPaint(new Color(0, 0, 0, 40));
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(unified);

            // Stripes
            g.setPaint(stripeColor);
            g.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new QuadCurve2D.Double(-24, -30, 0, -20, 24, -30));
            g.draw(new QuadCurve2D.Double(-26, 0, 0, 10, 26, 0));
            g.draw(new QuadCurve2D.Double(-22, 30, 0, 40, 22, 30));

            // Mouth
            double mh = 18 + 22 * mouthOpen;
            Shape mouth = new RoundRectangle2D.Double(-12, -12, 24, mh, 14, 14);
            g.setPaint(new Color(60, 30, 25));
            g.fill(mouth);
            g.setPaint(new Color(220, 80, 70));
            g.fill(new Ellipse2D.Double(-8, mh * 0.25 - 8, 16, 12));

            // Eyes
            drawEye(g, -14, -55, 16, blink);
            drawEye(g, 14, -55, 16, blink);

            // หนวดเล็ก
            g.setPaint(bodyColor.brighter());
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(new QuadCurve2D.Double(10, -82, 26, -98, 10, -110));
            g.fill(new Ellipse2D.Double(6, -116, 8, 8));

            g.setTransform(old);
            g.setComposite(oldC);
        }

        private void drawEye(Graphics2D g, double x, double y, double r, double blink) {
            g.setPaint(Color.WHITE);
            g.fill(new Ellipse2D.Double(x - r, y - r * blink, r * 2, r * 2 * blink));
            g.setPaint(Color.BLACK);
            g.setStroke(new BasicStroke(1.4f));
            g.draw(new Ellipse2D.Double(x - r, y - r * blink, r * 2, r * 2 * blink));
            double pr = Math.max(3, r * 0.45 * blink);
            g.fill(new Ellipse2D.Double(x - pr / 2, y - pr / 2, pr, pr));
            g.setPaint(new Color(255, 255, 255, 180));
            g.fill(new Ellipse2D.Double(x - pr * 0.35, y - pr * 0.35, pr * 0.35, pr * 0.35));
            drawTear(g, x, y + r * blink * 0.8, r * 0.4);
        }

        private void drawTear(Graphics2D g, double cx, double cy, double size) {
            size *= 2.8;
            g.setPaint(new Color(100, 180, 255, 180)); // สีน้ำเงินใส
            Path2D tear = new Path2D.Double();
            tear.moveTo(cx, cy);
            tear.curveTo(cx - size / 2, cy + size, cx + size / 2, cy + size, cx, cy + size * 1.5);
            tear.closePath();
            g.fill(tear);
        }

        private void drawScaledCircleShadow(Graphics2D g, int cx, int cy, int r) {
            AffineTransform old = g.getTransform();
            g.translate(cx, cy);
            g.scale(1.6, 0.7);
            drawMidpointCircle(g, 0, 0, r, new Color(0, 0, 0, 55));
            g.setTransform(old);
        }

        private void drawMidpointCircle(Graphics2D g, int cx, int cy, int r, Color c) {
            g.setPaint(c);
            int x = 0, y = r;
            int d = 1 - r;
            plotCircle8(g, cx, cy, x, y);
            while (x < y) {
                if (d < 0)
                    d += 2 * x + 3;
                else {
                    d += 2 * (x - y) + 5;
                    y--;
                }
                x++;
                plotCircle8(g, cx, cy, x, y);
            }
        }

        private void plotCircle8(Graphics2D g, int cx, int cy, int x, int y) {
            g.fillRect(cx + x, cy + y, 1, 1);
            g.fillRect(cx - x, cy + y, 1, 1);
            g.fillRect(cx + x, cy - y, 1, 1);
            g.fillRect(cx - x, cy - y, 1, 1);
            g.fillRect(cx + y, cy + x, 1, 1);
            g.fillRect(cx - y, cy + x, 1, 1);
            g.fillRect(cx + y, cy - x, 1, 1);
            g.fillRect(cx - y, cy - x, 1, 1);
        }
    }
}
