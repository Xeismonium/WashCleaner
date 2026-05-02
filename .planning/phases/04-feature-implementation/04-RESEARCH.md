# Phase 4: Feature Implementation - Research

**Researched:** May 1, 2026
**Domain:** Jetpack Compose Advanced UI, Material 3 Inputs, Vico Charts, Navigation Patterns
**Confidence:** HIGH

## Summary

This research establishes the implementation patterns for the extensive feature set in Milestone v1.3. We will utilize advanced Compose list optimizations, complex Material 3 input components (Autocomplete, DatePicker), and the Vico charting library for financial reporting.

## Advanced UI Patterns

### List Optimization (LazyColumn/LazyRow)
- **Stable Keys**: Use the `key` parameter in `items()` (e.g., `it.id`) to ensure state is preserved during list reordering and to optimize recompositions.
- **State Hoisting**: Hoist `LazyListState` to screen-level when coordination between list and other components (like scroll-to-top or search bar visibility) is needed.

### Material 3 Inputs
- **Autocomplete**: Use `ExposedDropdownMenuBox` with a `TextField` and `DropdownMenu`. Filter suggestions in the ViewModel as the user types (at least 2 characters).
- **DatePicker**: Use the Material 3 `DatePickerDialog` or `DatePickerModal`. Tapping a read-only `OutlinedTextField` should trigger the modal via `pointerInput` or a dedicated `IconButton`.
- **Chips**: Use `FilterChip` for status filtering and `AssistChip` for subtle info tags (like order count).

## Data Visualization (Vico Charts)

### ColumnChart Implementation
- **Model Producer**: Use `CartesianChartModelProducer` to manage the chart data lifecycle.
- **Transactions**: Data updates must be run within `modelProducer.runTransaction`.
- **Customization**:
    - **Y-Axis**: Implement a custom `VerticalAxis.rememberStart(valueFormatter = ...)` to format large numeric values as Rupiah (e.g., "1M" or formatted amounts).
    - **X-Axis**: Use `HorizontalAxis.rememberBottom(valueFormatter = ...)` to display dates or period labels.

## Navigation & Architecture

### Dependency Injection
- **hiltViewModel()**: Always use `hiltViewModel()` within the `[Name]Screen` function to obtain a ViewModel instance correctly scoped to the current navigation back stack entry.

### Navigation Logic
- **PopBackStack / NavigateUp**: Use `navController.popBackStack()` for simple "back" actions and `navController.navigateUp()` for hierarchical up navigation.
- **Arguments**: Pass IDs (orderId, customerId) as path arguments in the route string. Retrieve them in the `NavGraph` and pass them to the `Screen` composable.

## Code Patterns

### 1. The Autocomplete Dropdown
```kotlin
@Composable
fun AutocompleteDropdown(
    label: String,
    suggestions: List<String>,
    onQueryChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = query,
            onValueChange = { 
                onQueryChange(it)
                expanded = it.length >= 2
            },
            label = { Text(label) }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            suggestions.forEach { selection ->
                DropdownMenuItem(
                    text = { Text(selection) },
                    onClick = { /* Handle selection */ }
                )
            }
        }
    }
}
```

### 2. Vico Chart in Compose
```kotlin
CartesianChartHost(
    chart = rememberCartesianChart(
        rememberColumnCartesianLayer(),
        startAxis = VerticalAxis.rememberStart(
            valueFormatter = { value, _ -> formatRupiah(value.toLong()) }
        ),
        bottomAxis = HorizontalAxis.rememberBottom(),
    ),
    modelProducer = modelProducer,
)
```

## Known Pitfalls
- **Expensive Calculations**: Use `derivedStateOf` to limit recompositions when a state (like the total price) depends on multiple other states (weight, service price) that change rapidly.
- **Memory Management**: Avoid large allocations inside `LazyColumn` item composables; pre-process formatting (like Rupiah strings) in the data layer or ViewModel if possible.

## Metadata
**Confidence breakdown:**
- Compose Lists/Inputs: HIGH
- Vico API: HIGH
- Navigation: HIGH

**Research date:** May 1, 2026
