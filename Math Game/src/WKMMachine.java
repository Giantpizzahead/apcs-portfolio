/**
 * (Project 2/3: Math Game)
 * 
 * A simple game where you answer math questions. This project showcases
 * the swing GUI layout / interaction, and it remembers how many questions
 * you got right or wrong.
 * 
 * @author Kyle Fu
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class WKMMachine {
    public static void main(String[] args) throws IOException {
        new WKMMachine();
    }

    public WKMMachine() throws IOException {
        InputStream inStream = getClass().getResourceAsStream("dothemath.jpg");
        BufferedImage titleImg = ImageIO.read(inStream);
        ImageIcon titleIcon = new ImageIcon(titleImg);
        inStream.close();

        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel();
        label.setIcon(titleIcon);

        ProblemPanel panel = new ProblemPanel();

        frame.add(label);
        frame.add(panel);
        frame.setVisible(true);
    }
}

class ProblemPanel extends JPanel {
    private JLabel display, status;
    private JTextField answerField;
    private JButton button;

    private Solver solver = new Solver();
    private List<String> problem;
    private int numCorrect = 0;
    private int numWrong = 0;
    private String response = "";

    public ProblemPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.LIGHT_GRAY));

        display = new JLabel();
        display.setHorizontalAlignment(JLabel.CENTER);
        display.setPreferredSize(new Dimension(500, 180));
        display.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        showProblem();
        add(display, BorderLayout.NORTH);

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.add(new JLabel("Enter Answer: "));
        answerField = new JTextField(4);
        bottom.add(answerField);
        button = new JButton("Submit");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                buttonPressed();
            }
        });

        bottom.add(button);
        add(bottom, BorderLayout.CENTER);

        status = new JLabel();
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setPreferredSize(new Dimension(500, 80));
        add(status, BorderLayout.SOUTH);
    }

    public void showProblem() {
        problem = solver.createMathProblem();
        display.setText("<html><center><h3>" + response +
                "</h3><h3><font color=blue>Question</font></h3><br><big><b><font color=red>" +
                problem.get(0) + " " + problem.get(1) + " " + problem.get(2) +
                "</font></b></big></center></html>");
    }

    public void buttonPressed() {
        answerField.requestFocus();

        String userInput = answerField.getText().trim();
        if (userInput.length() == 0) {
            errorMessage("Enter your answer and then submit.");
            return;
        }

        int userAnswer;
        try {
            userAnswer = Integer.parseInt(userInput);
        } catch (NumberFormatException e) {
            errorMessage("\"" + userInput + "\" is not a valid number.");
            return;
        }

        if (solver.isAnswerCorrect(userAnswer)) {
            response = "Correct!";
            numCorrect++;
        } else {
            response = "Sorry, the correct answer was " + solver.getAnswer();
            numWrong++;
        }

        status.setText("<html><center>" + "Number of correct answers: " + numCorrect + "<br>Number of wrong answers: " + numWrong + "</center></html>");

        showProblem();
        answerField.setText("");
    }

    public void errorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Uh-oh!", JOptionPane.ERROR_MESSAGE);
    }
}