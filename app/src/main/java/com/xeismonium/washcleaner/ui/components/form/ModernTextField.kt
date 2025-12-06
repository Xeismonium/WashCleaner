package com.xeismonium.washcleaner.ui.components.form

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isRequired: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colorScheme = MaterialTheme.colorScheme

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> colorScheme.error
            isFocused -> colorScheme.primary
            else -> colorScheme.outline.copy(alpha = 0.5f)
        },
        animationSpec = tween(200),
        label = "borderColor"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused || isError) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "borderWidth"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isFocused -> colorScheme.primaryContainer.copy(alpha = 0.08f)
            else -> colorScheme.surfaceVariant.copy(alpha = 0.3f)
        },
        animationSpec = tween(200),
        label = "backgroundColor"
    )

    val iconColor by animateColorAsState(
        targetValue = when {
            isError -> colorScheme.error
            isFocused -> colorScheme.primary
            else -> colorScheme.onSurfaceVariant
        },
        animationSpec = tween(200),
        label = "iconColor"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // Label
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = when {
                    isError -> colorScheme.error
                    isFocused -> colorScheme.primary
                    else -> colorScheme.onSurfaceVariant
                }
            )
            if (isRequired) {
                Text(
                    text = " *",
                    style = MaterialTheme.typography.labelLarge,
                    color = colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(borderWidth, borderColor, RoundedCornerShape(12.dp)),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = colorScheme.onSurface
            ),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = if (singleLine) 14.dp else 12.dp),
                    verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )

        // Error Message
        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}
