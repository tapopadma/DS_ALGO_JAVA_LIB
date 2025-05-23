package ds.algo.java.lib.algorithms;

import ds.algo.java.lib.io.FastInputReader;
import java.io.PrintWriter;
import java.util.Stack;
import java.lang.Math;

/** Infix to postfix then evaluate postfix to evaluate expressions. */
public class ArithmeticExpression implements StandardAlgoSolver {

  char[] operators =
      new char[] {
        '^', '*', '/', '%', '+', '-',
      };

  protected int getPrecedence(char c) {
    for (int i = 0; i < operators.length; i++) {
      if (operators[i] == c) {
        return operators.length - i;
      }
    }
    return -1;
  }

  protected boolean isOperand(char c) {
    return c >= '0' && c <= '9';
  }

  protected int calculate(int x, int y, char c) {
    switch (c) {
      case '+':
        return x + y;
      case '-':
        return x - y;
      case '*':
        return x * y;
      case '/':
        return x / y;
      case '%':
        return x % y;
      case '^':
        return (int) Math.pow(x, y);
      default:
        throw new RuntimeException("Invalid operator: " + c);
    }
  }

  // similar to prefixToPostFix except the operator is inserted in between the operands with ().
  protected String prefixToInfix(String expr) {
    Stack<String> q = new Stack<>();
    for (int i = expr.length() - 1; i >= 0; i--) {
      char c = expr.charAt(i);
      if (isOperand(c)) {
        q.add(String.valueOf(c));
      } else {
        q.add("(" + q.pop() + c + q.pop() + ")");
      }
    }
    return q.pop();
  }

  // Prefix is created from infix with a similar analogy to postfix but in a reverse order.
  // Process the prefix in reverse order and append the operands to the stack.
  // When an operator is encountered, pop two operands from the stack and concatenate them in the
  // postfix format i.e. operand,operand,operator. Push it back to the stack.
  // Repeat this process until the entire expression has been evaluated. Return stack top.
  protected String prefixToPostFix(String expr) {
    Stack<String> q = new Stack<>();
    for (int i = expr.length() - 1; i >= 0; i--) {
      char c = expr.charAt(i);
      if (isOperand(c)) {
        q.add(String.valueOf(c));
      } else {
        q.add(q.pop() + q.pop() + c);
      }
    }
    return q.pop();
  }

  // Keep appending characters to the stack until you find an operator.
  // Then, pop two numbers from the stack and calculate the result using the operator.
  // Repeat this process until you have evaluated the entire expression.
  protected int evaluatePostfix(String expr) {
    Stack<String> q = new Stack<>();
    for (int i = 0; i < expr.length(); i++) {
      char c = expr.charAt(i);
      if (isOperand(c)) {
        q.add(String.valueOf(c));
      } else {
        int y = Integer.parseInt(q.pop());
        int x = Integer.parseInt(q.pop());
        q.add(String.valueOf(calculate(x, y, c)));
      }
    }
    return Integer.parseInt(q.pop());
  }

  // the operands are added to the output (i.e. postfix) normally, but the operators are ordered
  // based on the order
  // of precedence and brackets. This ordering is handled using a stack such that higher precedence
  // operators are added to the output first.
  protected String infixToPostfix(String expr) {
    Stack<Character> q = new Stack<>();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < expr.length(); i++) {
      char c = expr.charAt(i);
      if (isOperand(c)) {
        sb.append(c);
      } else {
        if (q.isEmpty() || c == '(' || getPrecedence(c) > getPrecedence(q.peek())) {
          q.add(c);
        } else if (c == ')') {
          while (true) {
            char c1 = q.pop();
            if (c1 == '(') {
              break;
            }
            sb.append(c1);
          }
        } else {
          while (!q.isEmpty() && getPrecedence(c) < getPrecedence(q.peek())) {
            sb.append(q.pop());
          }
          q.add(c);
        }
      }
    }
    while (!q.isEmpty()) {
      sb.append(q.pop());
    }
    System.out.println("postfix: " + sb.toString());
    return sb.toString();
  }

  protected int evaluate(String expr) {
    return evaluatePostfix(infixToPostfix(expr));
  }

  @Override
  public void solve(FastInputReader in, PrintWriter out) {
    validateLogic(evaluate("1+2"), 3);
    validateLogic(evaluate("3+2*(2^2-1)^(1+1*2)-9"), 48);
    validateLogic(evaluate("3+3+((2^4)-6)-(2+3)"), 11);
    validateLogic(evaluate("2+4*5"), 22);
    validateLogic(prefixToPostFix("*+23-14"), "23+14-*");
    validateLogic(prefixToPostFix("*-5/42-/621"), "542/-62/1-*");
    validateLogic(prefixToInfix("+1*23"), "(1+(2*3))");
    validateLogic(prefixToInfix("-+1*2^-^222+1*121"), "((1+(2*(((2^2)-2)^(1+(1*2)))))-1)");
  }
}
