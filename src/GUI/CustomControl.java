/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

/**
 *
 * @author abbas
 */
public class CustomControl {

    private Shape shape;

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    private boolean state;
    private static Color selected_color = Color.GREEN;
    private static Color un_selected_color = Color.RED;
    private static Stroke selected_stroke = new BasicStroke(8f);
    private static Stroke un_selected_stroke = new BasicStroke(8f);

    public CustomControl(Shape shape) {
        this.shape = shape;
    }

    public Color getColor() {
        Color col = selected_color;
        if (!state) {
            col = un_selected_color;
        }

        return col;
    }

    public Stroke getStroke() {
        Stroke str = selected_stroke;
        if (!state) {
            str = un_selected_stroke;
        }

        return str;
    }

}
