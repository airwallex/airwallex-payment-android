package com.airwallex.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPostfixExpression

class NotNullAssertionOperatorRule : Rule() {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Disallow to cast to nullable types",
        Debt.FIVE_MINS
    )

    override fun visitPostfixExpression(expression: KtPostfixExpression) {
        if (expression.operationToken == KtTokens.EXCLEXCL) {
            report(
                CodeSmell(issue, Entity.from(expression),
                "Not-null assertion operator (!!) is dangerous. Unwrap, or throw IllegalArgumentException instead.")
            )
        }
        super.visitPostfixExpression(expression)
    }
}
