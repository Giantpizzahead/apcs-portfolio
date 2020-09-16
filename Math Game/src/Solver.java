import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Solver {
    private int answer;

    public List<String> createMathProblem() {
        Random rnd = new Random();
        int num1 = rnd.nextInt(101);
        int num2 = rnd.nextInt(101);
        int oper = rnd.nextInt(2);
        String operStr;

        if (oper == 0) {
            operStr = "+";
            answer = num1 + num2;
        } else {
            operStr = "-";
            answer = num1 - num2;
        }

        String num1Str = String.valueOf(num1);
        String num2Str = String.valueOf(num2);

        List<String> problem = new ArrayList<>(3);
        problem.add(num1Str);
        problem.add(operStr);
        problem.add(num2Str);
        return problem;
    }

    public boolean isAnswerCorrect(int userAnswer) {
        return this.answer == userAnswer;
    }

    public int getAnswer() {
        return this.answer;
    }
}
