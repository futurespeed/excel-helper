package org.fs.validation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SpelConstraintValidator implements ConstraintValidator<Spel, String> {

    private static ExpressionParser parser = new SpelExpressionParser();

    private Spel spel;

    @Override
    public void initialize(Spel spel) {
        this.spel = spel;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        StandardEvaluationContext ec = new StandardEvaluationContext(value);
        ApplicationContext applicationContext = null;//TODO SpringContextHolder.getApplicationContext()
        ec.setBeanResolver(new BeanFactoryResolver(applicationContext));
        Expression expression = parser.parseExpression(spel.value());
        return expression.getValue(ec, Object.class) != null;
    }
}
