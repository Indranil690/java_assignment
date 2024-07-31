import java.util.Scanner;

public class calculator {

    public static void main(String[] args) {
       while(true){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter an operator (+, -, *, /):");
        System.out.println("Enter e for exit");
        char operator = scanner.next().charAt(0);
        double result;
        double num1; 
        double num2;

        switch (operator) {
            case '+':
                System.out.println("Enter first number:");
                num1 = scanner.nextDouble();
                System.out.println("Enter second number:");
                num2 = scanner.nextDouble();
                result = num1 + num2;
                System.out.println("Result is: "+ result);
                break;
            case '-':
                System.out.println("Enter first number:");
                num1 = scanner.nextDouble();
                System.out.println("Enter second number:");
                num2 = scanner.nextDouble();
                result = num1 - num2;
                System.out.println("Result is: "+ result);
                break;
            case '*':
                System.out.println("Enter first number:");
                num1 = scanner.nextDouble();
                System.out.println("Enter second number:");
                num2 = scanner.nextDouble();
                result = num1 * num2;
                System.out.println("Result is: "+ result);
                break;
            case '/':
                System.out.println("Enter first number:");
                num1 = scanner.nextDouble();
                System.out.println("Enter second number:");
                num2 = scanner.nextDouble();
                if (num2 != 0) {
                    result = num1 / num2;
                    System.out.println("Result is: "+ result);
                } else {
                    System.out.println("Error! Division by zero.");
                    return;
                }
                System.out.println(result);
                break;
            case 'e':
                return;
            default:
                System.out.println("Error! Invalid operator.");
                return;
        }
       }
    }
}