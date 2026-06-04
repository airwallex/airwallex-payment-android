//[ui-core](../../../index.md)/[com.airwallex.android.ui.composables](../index.md)/[StandardTextFieldOptions](index.md)

# StandardTextFieldOptions

[androidJvm]\
data class [StandardTextFieldOptions](index.md)(val singleLine: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true, val inputType: [StandardTextFieldOptions.InputType](-input-type/index.md) = InputType.NORMAL, val returnType: [StandardTextFieldOptions.ReturnType](-return-type/index.md) = ReturnType.DONE)

## Constructors

| | |
|---|---|
| [StandardTextFieldOptions](-standard-text-field-options.md) | [androidJvm]<br>constructor(singleLine: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true, inputType: [StandardTextFieldOptions.InputType](-input-type/index.md) = InputType.NORMAL, returnType: [StandardTextFieldOptions.ReturnType](-return-type/index.md) = ReturnType.DONE) |

## Types

| Name | Summary |
|---|---|
| [InputType](-input-type/index.md) | [androidJvm]<br>enum [InputType](-input-type/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[StandardTextFieldOptions.InputType](-input-type/index.md)&gt; <br>See [android.widget.TextView.getInputType](https://developer.android.com/reference/kotlin/android/widget/TextView.html#getinputtype) for details. |
| [ReturnType](-return-type/index.md) | [androidJvm]<br>enum [ReturnType](-return-type/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[StandardTextFieldOptions.ReturnType](-return-type/index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [inputType](input-type.md) | [androidJvm]<br>val [inputType](input-type.md): [StandardTextFieldOptions.InputType](-input-type/index.md) |
| [returnType](return-type.md) | [androidJvm]<br>val [returnType](return-type.md): [StandardTextFieldOptions.ReturnType](-return-type/index.md) |
| [singleLine](single-line.md) | [androidJvm]<br>val [singleLine](single-line.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true |

## Functions

| Name | Summary |
|---|---|
| [makeKeyboardOptions](make-keyboard-options.md) | [androidJvm]<br>fun [makeKeyboardOptions](make-keyboard-options.md)(): [KeyboardOptions](https://developer.android.com/reference/kotlin/androidx/compose/foundation/text/KeyboardOptions.html) |
