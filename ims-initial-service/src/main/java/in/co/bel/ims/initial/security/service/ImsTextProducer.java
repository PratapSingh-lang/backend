package in.co.bel.ims.initial.security.service;


import java.security.SecureRandom;
import java.util.Random;

import cn.apiclub.captcha.text.producer.DefaultTextProducer;

/**
 * Produces text of a given length from a given array of characters.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * 
 */
public class ImsTextProducer extends DefaultTextProducer {

    private static final Random RAND = new SecureRandom();
    private static final int DEFAULT_LENGTH = 5;
    private static final char[] DEFAULT_CHARS = new char[] { '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    private final int _length;
    private final char[] _srcChars;

    public ImsTextProducer() {
    	this(DEFAULT_LENGTH, DEFAULT_CHARS);
    }
    
    public ImsTextProducer(int length, char[] srcChars) {
    	_length = length;
    	_srcChars = copyOf(srcChars, srcChars.length);
    }
    
    @Override
    public String getText() {
        String capText = "";
        String ints = "123456789";
        int[] intArray = new int[]{ 1,2,3,4,5,6,7,8,9 }; 
        char[] operators = { '+', '-' };
        Random random = new SecureRandom();
        char op = operators[random.nextInt(operators.length)];
        int numOne = intArray[RAND.nextInt(ints.length())];
        int numTwo = 0;
        if(op == '+') {
        	numTwo = RAND.nextInt(10);
        }else {
        	numTwo = RAND.nextInt(numOne);
        }
        capText += numOne + " " + op + " " + numTwo;
        return capText;
    }
    
    private static char[] copyOf(char[] original, int newLength) {
        char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0,
                Math.min(original.length, newLength));
        return copy;
    }
}
