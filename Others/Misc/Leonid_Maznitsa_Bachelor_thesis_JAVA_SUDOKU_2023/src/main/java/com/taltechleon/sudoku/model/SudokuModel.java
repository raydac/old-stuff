package com.taltechleon.sudoku.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SudokuModel {
  private final byte[] sudokuField;
  private final int subFieldSize;
  private final int subFields;

  private final int edgeSize;

  private final int sumOfAllValues;
  private final int totalCells;

  private SudokuModel(final SudokuModel solver) {
    this(solver.subFieldSize, solver.subFields);
    System.arraycopy(solver.sudokuField, 0, this.sudokuField, 0, this.totalCells);
  }

  public SudokuModel(final int subfieldSize, final int subFields) {
    this.subFieldSize = subfieldSize;
    this.subFields = subFields;
    this.edgeSize = subfieldSize * subFields;
    this.totalCells = this.edgeSize * this.edgeSize;
    this.sudokuField = new byte[this.totalCells];

    if (this.edgeSize > 32) {
      throw new IllegalArgumentException("Too big number of variants, rework needed");
    }
    this.sumOfAllValues = (this.edgeSize * (this.edgeSize + 1)) / 2;
  }

  private static int[] extractNonZeroValues(final int[] valuesWithZero) {
    int countValues = 0;
    for (int j : valuesWithZero) {
      if (j != 0) {
        countValues++;
      }
    }
    int[] resultVariants = new int[countValues];
    int index = 0;
    for (int j : valuesWithZero) {
      if (j != 0) {
        resultVariants[index++] = j;
      }
    }
    return resultVariants;
  }

  public void setCellValue(final int col, final int row, final int value) {
    if (col < 0 || col >= this.edgeSize) {
      throw new IllegalArgumentException("Column must be in 0.." + (this.edgeSize - 1));
    }
    if (row < 0 || row >= this.edgeSize) {
      throw new IllegalArgumentException("Row must be in 0.." + (this.edgeSize - 1));
    }
    if (value < 0 || value > this.edgeSize) {
      throw new IllegalArgumentException(
          "Value must be in 0.." + (this.edgeSize - 1) + ": " + value);
    }
    this.sudokuField[col + row * this.edgeSize] = (byte) value;
  }

  private SudokuModel transpose() {
    final byte[] newArray = Arrays.copyOf(this.sudokuField, this.sudokuField.length);
    for (int row = 0; row < this.edgeSize; row++) {
      for (int col = 0; col < this.edgeSize; col++) {
        final byte value = this.sudokuField[col + row * this.edgeSize];
        newArray[col * this.edgeSize + row] = value;
      }
    }
    System.arraycopy(newArray, 0, this.sudokuField, 0, newArray.length);
    return this;
  }

  private SudokuModel swapRows(final int subFieldIndex, final int row1, final int row2) {
    if (row1 < 0 || row1 >= this.subFieldSize) {
      throw new IllegalArgumentException("Wrong row1");
    }
    if (row2 < 0 || row2 >= this.subFieldSize) {
      throw new IllegalArgumentException("Wrong row2");
    }
    if (subFieldIndex < 0 || subFieldIndex >= this.subFields) {
      throw new IllegalArgumentException("Wrong sub field");
    }

    if (row1 != row2) {
      int offsetRow1 = row1 * this.edgeSize + subFieldIndex * this.subFieldSize * this.edgeSize;
      int offsetRow2 = row2 * this.edgeSize + subFieldIndex * this.subFieldSize * this.edgeSize;
      for (int i = 0; i < this.edgeSize; i++) {
        final byte a = this.sudokuField[offsetRow1];
        final byte b = this.sudokuField[offsetRow2];
        this.sudokuField[offsetRow1] = b;
        this.sudokuField[offsetRow2] = a;
        offsetRow1++;
        offsetRow2++;
      }
    }

    return this;
  }

  private SudokuModel swapSubFieldCols(final int subField1, final int subField2) {
    if (subField1 < 0 || subField1 >= this.subFields) {
      throw new IllegalArgumentException("Wrong sub field 1");
    }
    if (subField2 < 0 || subField2 >= this.subFields) {
      throw new IllegalArgumentException("Wrong sub field 2");
    }

    if (subField1 != subField2) {
      int offset1 = subField1 * this.subFieldSize;
      int offset2 = subField2 * this.subFieldSize;

      for (int i = 0; i < this.edgeSize; i++) {
        for (int j = 0; j < this.subFieldSize; j++) {
          final int offsetA = offset1 + j;
          final int offsetB = offset2 + j;

          final byte a = this.sudokuField[offsetA];
          final byte b = this.sudokuField[offsetB];
          this.sudokuField[offsetA] = b;
          this.sudokuField[offsetB] = a;
        }
        offset1 += this.edgeSize;
        offset2 += this.edgeSize;
      }
    }

    return this;
  }

  private SudokuModel swapSubFieldRows(final int subField1, final int subField2) {
    if (subField1 < 0 || subField1 >= this.subFields) {
      throw new IllegalArgumentException("Wrong sub field 1");
    }
    if (subField2 < 0 || subField2 >= this.subFields) {
      throw new IllegalArgumentException("Wrong sub field 2");
    }

    if (subField1 != subField2) {
      int offset1 = subField1 * this.subFieldSize * this.edgeSize;
      int offset2 = subField2 * this.subFieldSize * this.edgeSize;

      for (int i = 0; i < this.edgeSize * this.subFieldSize; i++) {
        final byte a = this.sudokuField[offset1];
        final byte b = this.sudokuField[offset2];
        this.sudokuField[offset1] = b;
        this.sudokuField[offset2] = a;
        offset1++;
        offset2++;
      }
    }
    return this;
  }

  private SudokuModel swapColumns(final int subField, final int col1, final int col2) {
    if (col1 < 0 || col1 >= this.subFieldSize) {
      throw new IllegalArgumentException("Wrong col1");
    }
    if (col2 < 0 || col2 >= this.subFieldSize) {
      throw new IllegalArgumentException("Wrong col2");
    }
    if (subField < 0 || subField >= this.subFields) {
      throw new IllegalArgumentException("Wrong sub field");
    }

    if (col1 != col2) {
      int offsetCol1 = col1 + subField * this.subFieldSize;
      int offsetCol2 = col2 + subField * this.subFieldSize;
      for (int i = 0; i < this.edgeSize; i++) {
        final byte a = this.sudokuField[offsetCol1];
        final byte b = this.sudokuField[offsetCol2];
        this.sudokuField[offsetCol1] = b;
        this.sudokuField[offsetCol2] = a;
        offsetCol1 += this.edgeSize;
        offsetCol2 += this.edgeSize;
      }
    }

    return this;
  }

  public void generate(final int clues) {
    if (clues < 17) {
      throw new IllegalArgumentException(
          "Number of clues can't be less than 17, see https://en.wikipedia.org/wiki/Mathematics_of_Sudoku");
    }

    final Random rnd = ThreadLocalRandom.current();

    List<Integer> nonEmptyOffsets = new ArrayList<>();
    final int MAX_ATTEMPTS_BEFORE_REGENERATION = 50;

    boolean found = false;
    while (!found) {
      this.fillBase();
      for (int i = 0; i < 500_000; i++) {
        switch (rnd.nextInt(5)) {
          case 0:
            this.transpose();
            break;
          case 1:
            this.swapSubFieldCols(rnd.nextInt(this.subFields), rnd.nextInt(this.subFields));
            break;
          case 2:
            this.swapSubFieldRows(rnd.nextInt(this.subFields), rnd.nextInt(this.subFields));
            break;
          case 3:
            this.swapColumns(rnd.nextInt(this.subFields), rnd.nextInt(this.subFieldSize),
                rnd.nextInt(this.subFieldSize));
            break;
          case 4:
            this.swapRows(rnd.nextInt(this.subFields), rnd.nextInt(this.subFieldSize),
                rnd.nextInt(this.subFieldSize));
            break;
        }
      }

      int cellsToRemove = this.totalCells - clues;
      int attempts = MAX_ATTEMPTS_BEFORE_REGENERATION;
      while (cellsToRemove > 0) {
        nonEmptyOffsets.clear();
        for (int i = 0; i < this.totalCells; i++) {
          if (this.sudokuField[i] != 0) {
            nonEmptyOffsets.add(i);
          }
        }
        if (nonEmptyOffsets.isEmpty()) {
          break;
        }
        Collections.shuffle(nonEmptyOffsets, rnd);
        final int randomOffset = nonEmptyOffsets.get(0);

        final byte cellValue = this.sudokuField[randomOffset];
        this.sudokuField[randomOffset] = 0;

        Set<SudokuModel> solutions = this.solve(2);

        if (solutions.size() != 1) {
          // problem, no unique solution so we should try another cell
          this.sudokuField[randomOffset] = cellValue;
          attempts--;
          if (attempts == 0) {
            // too many attempts, looks like too big problem we should regenerate field and remake steps
            found = false;
            break;
          }
        } else {
          // ok, we ca move forward
          cellsToRemove--;
          attempts = MAX_ATTEMPTS_BEFORE_REGENERATION;
          found = true;
        }
      }
    }
  }

  public SudokuModel fillBase() {
    final List<Integer> values =
        IntStream.range(1, this.edgeSize + 1).boxed().collect(Collectors.toCollection(
            ArrayList::new));
    for (int subField = 0; subField < this.subFields; subField++, Collections.rotate(values, -1)) {
      for (int row = 0; row < this.subFieldSize; row++) {
        int offset = subField * this.subFieldSize * this.edgeSize + row * this.edgeSize;
        for (int v : values) {
          this.sudokuField[offset++] = (byte) v;
        }
        Collections.rotate(values, -this.subFieldSize);
      }
    }
    return this;
  }

  public int getCellValue(final int col, final int row) {
    if (col < 0 || col >= this.edgeSize) {
      throw new IllegalArgumentException("Column must be in 0.." + (this.edgeSize - 1));
    }
    if (row < 0 || row >= this.edgeSize) {
      throw new IllegalArgumentException("Row must be in 0.." + (this.edgeSize - 1));
    }
    return this.sudokuField[col + row * this.edgeSize] & 0xFF;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (obj instanceof SudokuModel that) {
      return Arrays.equals(this.sudokuField, that.sudokuField);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int sum = 0;
    for (byte b : this.sudokuField) {
      sum += b & 0xFF;
    }
    return sum;
  }

  private Set<SudokuModel> solveDepthFirstAndBacktrack(final List<CellVariant> cellVariants,
                                                       final int limitSolutions,
                                                       final Consumer<SudokuModel> foundVariantConsumer) {

    if (cellVariants.isEmpty()) {
      return Set.of();
    }
    final CellVariant head = cellVariants.get(0);

    Set<SudokuModel> result = null;

    for (final int v : head.variants) {
      this.sudokuField[head.offset] = (byte) v;
      final List<CellVariant> restVariants = this.findCurrentVariantsAndSortThem();
      if (restVariants.isEmpty()) {
        if (this.isCorrectlyFilled()) {
          if (result == null) {
            result = new HashSet<>();
          }
          final SudokuModel foundVariant = new SudokuModel(this);
          result.add(foundVariant);
          foundVariantConsumer.accept(foundVariant);
        }
      } else {
        final Set<SudokuModel> solutions =
            this.solveDepthFirstAndBacktrack(restVariants, limitSolutions, foundVariantConsumer);
        if (!solutions.isEmpty()) {
          if (result == null) {
            result = new HashSet<>();
          }
          result.addAll(solutions);
        }
      }
      this.sudokuField[head.offset] = 0;
      if (result != null && result.size() >= limitSolutions) {
        break;
      }
    }

    this.sudokuField[head.offset] = 0;
    return result == null ? Set.of() : result;
  }

  public Set<SudokuModel> solve(final int limitSolutions) {
    return this.solve(limitSolutions, e -> {
    });
  }

  public Set<SudokuModel> solve(final int limitSolutions,
                                final Consumer<SudokuModel> foundVariantConsumer) {
    final List<CellVariant> initialVariants = findCurrentVariantsAndSortThem();

    final Set<SudokuModel> foundSolutions =
        this.solveDepthFirstAndBacktrack(initialVariants, limitSolutions, foundVariantConsumer);
    for (CellVariant v : initialVariants) {
      this.sudokuField[v.offset] = 0;
    }
    return foundSolutions;
  }


  public List<CellVariant> findCurrentVariantsAndSortThem() {
    final List<CellVariant> result = new ArrayList<>(this.totalCells);
    for (int offset = 0; offset < this.totalCells; offset++) {
      if (this.sudokuField[offset] == 0) {
        var variantsAndRatio = this.findPossibleCellValues(offset);
        result.add(new CellVariant(
            offset % this.edgeSize,
            offset / this.edgeSize,
            offset,
            variantsAndRatio.variants,
            variantsAndRatio.fillRatio));
      }
    }
    Arrays.sort(result.toArray(), 0, result.size());
    Collections.sort(result);
    return result;
  }


  private FoundVariantsAndFillRatio findPossibleCellValues(final int cellOffset) {
    if (this.sudokuField[cellOffset] == 0) {
      final int cx = cellOffset % this.edgeSize;
      final int cy = cellOffset / this.edgeSize;
      final int[] allVariants =
          IntStream.range(1, this.edgeSize + 1).toArray();

      int startOffset = cy * this.edgeSize;
      for (int x = 0; x < this.edgeSize; x++) {
        final int v = this.sudokuField[startOffset + x] & 0xFF;
        if (v != 0) {
          allVariants[v - 1] = 0;
        }
      }

      startOffset = cx;
      for (int y = 0; y < this.edgeSize; y++) {
        final int v = this.sudokuField[startOffset] & 0xFF;
        startOffset += this.edgeSize;
        if (v != 0) {
          allVariants[v - 1] = 0;
        }
      }

      int fillRatio = 0;
      startOffset =
          (cx / this.subFieldSize) * this.subFieldSize +
              (cy / this.subFieldSize) * this.subFieldSize * this.edgeSize;
      for (int y = 0; y < this.subFieldSize; y++) {
        for (int x = 0; x < this.subFieldSize; x++) {
          final int v = this.sudokuField[startOffset + x] & 0xFF;
          if (v != 0) {
            fillRatio++;
            allVariants[v - 1] = 0;
          }
        }
        startOffset += this.edgeSize;
      }

      int[] resultVariants = extractNonZeroValues(allVariants);
      return new FoundVariantsAndFillRatio(resultVariants, fillRatio);
    } else {
      return null;
    }
  }

  public byte[] cellsAsArray() {
    return this.sudokuField.clone();
  }

  public String cellsAsText() {
    final StringBuilder buffer = new StringBuilder();
    int offset = 0;
    for (int y = 0; y < this.edgeSize; y++) {
      if (!buffer.isEmpty()) {
        buffer.append('\n');
      }
      if (y > 0 && y % this.subFieldSize == 0) {
        buffer.append('\n');
      }
      for (int x = 0; x < this.edgeSize; x++) {
        if (x > 0 && x % this.subFieldSize == 0) {
          buffer.append(' ');
        }
        final int value = this.sudokuField[offset++] & 0xFF;
        if (value == 0) {
          buffer.append('.');
        } else {
          buffer.append(Integer.toString(value, this.edgeSize + 1).toUpperCase(Locale.ENGLISH));
        }
      }
    }
    return buffer.toString();
  }

  @Override
  public String toString() {
    final StringBuilder buffer = new StringBuilder();

    buffer
        .append("Sudoku ").append(this.edgeSize).append('X').append(this.edgeSize)
        .append(System.lineSeparator())
        .append("-".repeat(Math.max(0, this.edgeSize + this.subFields - 1)))
        .append(System.lineSeparator());

    for (int y = 0; y < this.edgeSize; y++) {
      if (y != 0 && y % this.subFieldSize == 0) {
        for (int x = 0; x < this.edgeSize + this.subFieldSize - 1; x++) {
          buffer.append(x > 0 && (x + 1) % (this.subFieldSize + 1) == 0 ? '*' : '-');
        }
        buffer.append(System.lineSeparator());
      }
      final int lineOffset = y * this.edgeSize;
      for (int x = 0; x < this.edgeSize; x++) {
        if (x > 0 && x % this.subFieldSize == 0) {
          buffer.append('|');
        }
        final int value = this.sudokuField[x + lineOffset] & 0xFF;
        buffer.append(value == 0 ? "." : Integer.valueOf(value));
      }
      buffer.append(System.lineSeparator());
    }

    buffer
        .append("-".repeat(Math.max(0, this.edgeSize + this.subFields - 1)));

    return buffer.toString();
  }

  public void loadFromArray(final byte[] array) {
    if (this.sudokuField.length != array.length) {
      throw new IllegalArgumentException(
          "Unexpected data size: " + this.sudokuField.length + " <> " + array.length);
    }
    System.arraycopy(array, 0, this.sudokuField, 0, array.length);
  }

  public void loadFromText(final String fieldAsText) {
    final String[] lines = fieldAsText.split("\\n");

    int counterLines = 0;
    int offset = 0;

    for (String s : lines) {
      if (s.isBlank()) {
        continue;
      }

      int counterDigits = 0;

      for (char c : s.toCharArray()) {
        if (Character.isDigit(c) || Character.isAlphabetic(c)) {
          final int value = (byte) Integer.parseInt(Character.toString(c), this.edgeSize + 1);
          if (value < 0 || value > this.edgeSize) {
            throw new IllegalArgumentException(
                "Unexpected value, must be 0.." +
                    Integer.toString(this.edgeSize, this.edgeSize + 1) + ": " + c);
          }
          this.sudokuField[offset++] = (byte) value;
          counterDigits++;
        } else if (c == '.') {
          this.sudokuField[offset++] = 0;
          counterDigits++;
        } else if (!Character.isWhitespace(c)) {
          throw new IllegalArgumentException("Unexpected char: " + c);
        }
      }

      if (counterDigits != this.edgeSize) {
        throw new IllegalStateException("Unexpected number of values in a line: " + counterDigits);
      }
      counterLines++;
    }
    if (counterLines != this.edgeSize) {
      throw new IllegalStateException("Unexpected number of lines: " + counterLines);
    }
  }

  public boolean isNoEmptyCells() {
    for (byte b : this.sudokuField) {
      if (b == 0) {
        return false;
      }
    }
    return true;
  }

  private boolean isRowsInCorrectState() {
    for (int y = 0; y < this.edgeSize; y++) {
      int offset = y * this.edgeSize;
      int sum = 0;
      for (int x = 0; x < this.edgeSize; x++) {
        sum += this.sudokuField[offset++] & 0xFF;
      }
      if (sum != this.sumOfAllValues) {
        return false;
      }
    }
    return true;
  }

  private boolean isSubFieldsInCorrectState() {
    for (int sy = 0; sy < this.subFields; sy++) {
      for (int sx = 0; sx < this.subFields; sx++) {
        final int subFieldOffset = sy * this.subFieldSize * this.edgeSize + sx * this.subFieldSize;
        int sum = 0;
        for (int y = 0; y < this.subFieldSize; y++) {
          int offset = subFieldOffset + y * this.edgeSize;
          for (int x = 0; x < this.subFieldSize; x++) {
            sum += this.sudokuField[offset++] & 0xFF;
          }
        }
        if (sum != this.sumOfAllValues) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean isColumnsInCorrectState() {
    for (int x = 0; x < this.edgeSize; x++) {
      int offset = x;
      int sum = 0;
      for (int y = 0; y < this.edgeSize; y++) {
        sum += this.sudokuField[offset] & 0xFF;
        offset += this.edgeSize;
      }
      if (sum != this.sumOfAllValues) {
        return false;
      }
    }
    return true;
  }

  public List<Error> findErrors() {
    final List<Error> foundErrors = new ArrayList<>();

    final Map<Integer,Integer> foundValues = new HashMap<>();
    // check rows
    for (int row = 0; row < this.edgeSize; row++) {
      foundValues.clear();
      final int rowOffset = row * this.edgeSize;
      for (int col = 0; col < this.edgeSize; col++) {
        final int value = this.sudokuField[rowOffset+col] & 0xFF;
        if (value == 0) {
          continue;
        }
        foundValues.merge(value, 1, Integer::sum);
      }
      for (int col = 0; col < this.edgeSize; col++) {
        final int value = this.sudokuField[rowOffset+col] & 0xFF;
        if (value == 0) {
          continue;
        }
        if (foundValues.get(value) > 1) {
          foundErrors.add(new Error(ErrorType.ROW_ERROR, col, row));
        }
      }
    }

    // check columns
    for (int col = 0; col < this.edgeSize; col++) {
      foundValues.clear();
      int rowOffset = col;
      for (int row = 0; row < this.edgeSize; row++) {
        final int value = this.sudokuField[rowOffset] & 0xFF;
        rowOffset += this.edgeSize;
        if (value == 0) {
          continue;
        }
        foundValues.merge(value, 1, Integer::sum);
      }
      rowOffset = col;
      for (int row = 0; row < this.edgeSize; row++) {
        final int value = this.sudokuField[rowOffset] & 0xFF;
        rowOffset += this.edgeSize;
        if (value == 0) {
          continue;
        }
        if (foundValues.get(value) > 1) {
          foundErrors.add(new Error(ErrorType.COLUMN_ERROR, col, row));
        }
      }
    }

    // check sub-fields
    for (int y = 0; y < this.subFields; y++) {
      for (int x = 0; x < this.subFields; x++) {
        foundValues.clear();
        int offset = x * this.subFieldSize + y * this.subFieldSize * this.edgeSize;
        for (int row = 0; row < this.subFieldSize; row++) {
          for (int col = 0; col < this.subFieldSize; col++) {
            final int value = this.sudokuField[offset + col] & 0xFF;
            if (value == 0) {
              continue;
            }
            foundValues.merge(value, 1, Integer::sum);
          }
          offset += this.edgeSize;
        }
        offset = x * this.subFieldSize + y * this.subFieldSize * this.edgeSize;
        for (int row = 0; row < this.subFieldSize; row++) {
          for (int col = 0; col < this.subFieldSize; col++) {
            final int value = this.sudokuField[offset + col] & 0xFF;
            if (value == 0) {
              continue;
            }
            if (foundValues.get(value) > 1) {
              foundErrors.add(new Error(ErrorType.SUBFIELD_ERROR, x * this.subFieldSize + col,
                  y * this.subFieldSize + row));
            }
          }
          offset += this.edgeSize;
        }
      }
    }

    // check for no variant cells
    final List<SudokuModel.CellVariant> variants = this.findCurrentVariantsAndSortThem();
    if (!variants.isEmpty()) {
      for (final SudokuModel.CellVariant v : variants) {
        if (v.variants.length == 0) {
          foundErrors.add(new Error(ErrorType.NO_CELL_VARIANTS, v.col, v.row));
        }
      }
    }
    return foundErrors;
  }

  public boolean isCorrectlyFilled() {
    return this.isNoEmptyCells()
        && this.isRowsInCorrectState()
        && this.isColumnsInCorrectState()
        && this.isSubFieldsInCorrectState();
  }

  public enum ErrorType {
    ROW_ERROR,
    COLUMN_ERROR,
    SUBFIELD_ERROR,
    NO_CELL_VARIANTS
  }

  public static class Error {
    private final ErrorType type;
    private final int col;
    private final int row;

    Error(final ErrorType type, final int col, final int row) {
      this.type = type;
      this.col = col;
      this.row = row;
    }

    public ErrorType getType() {
      return this.type;
    }

    public int getColumn() {
      return this.col;
    }

    public int getRow() {
      return this.row;
    }

    @Override
    public String toString() {
      return "Error{" +
          "type=" + this.type +
          ", col=" + this.col +
          ", row=" + this.row +
          '}';
    }
  }

  private record FoundVariantsAndFillRatio(int[] variants, int fillRatio) {
  }

  public static final class CellVariant
      implements Comparable<CellVariant> {
    private final int col;
    private final int row;
    private final int offset;
    private final int[] variants;
    private final int subFieldFillRatio;

    public CellVariant(final int col, final int row, final int offset, final int[] variants,
                       final int subFieldFillRatio) {
      this.col = col;
      this.row = row;
      this.offset = offset;
      this.variants = variants;
      this.subFieldFillRatio = subFieldFillRatio;
    }

    @Override
    public int compareTo(final CellVariant that) {
      int result = Integer.compare(this.variants.length, that.variants.length);
      if (result == 0) {
        result = Integer.compare(that.subFieldFillRatio, this.subFieldFillRatio);
      }
      return result;
    }

    public int col() {
      return this.col;
    }

    public int row() {
      return this.row;
    }

    public int offset() {
      return this.offset;
    }

    public int[] variants() {
      return this.variants;
    }

    public int subFieldFillRatio() {
      return this.subFieldFillRatio;
    }
  }
}
