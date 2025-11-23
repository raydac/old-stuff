package com.taltechleon.sudoku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.taltechleon.sudoku.model.SudokuModel;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SudokuModelTest {

  private void assertOnlySolution(
      final int subFieldSize,
      final int subFields,
      final String field,
      final String expectedSolution
  ) {
    var sudoku = new SudokuModel(subFieldSize, subFields);
    sudoku.loadFromText(field);

    var errors = sudoku.findErrors();
    if (!errors.isEmpty()) {
      fail(() -> "Sudoku field errors: " + errors);
    }

    var solution =
        sudoku.solve(2);
    if (solution.isEmpty()) {
      throw new IllegalStateException("Can't find any solution for sudoku");
    }
    if (solution.size() != 1) {
      fail("Extected only solution but found " + solution.size());
    }

    var foundSolution =
        solution.stream().findFirst().orElseThrow().cellsAsText().replaceAll("\\s", "");
    assertEquals(expectedSolution.replaceAll("\\s", "").toUpperCase(Locale.ENGLISH),
        foundSolution);
  }

  @Test
  void testEquals() {
    final SudokuModel one = new SudokuModel(3, 3);
    one.loadFromText("""
        864 729 315
        537 681 942
        291 453 678
                
        978 132 456
        645 897 123
        312 564 789
                
        483 276 591
        756 918 234
        129 345 867
        """);

    final SudokuModel two = new SudokuModel(3, 3);
    two.loadFromText("""
        864 729 315
        537 681 942
        291 453 678
                
        978 132 456
        645 897 123
        312 564 789
                
        483 276 591
        756 918 234
        129 345 867
        """);

    final SudokuModel three = new SudokuModel(3, 3);
    three.loadFromText("""
        864 729 325
        537 681 942
        291 453 678
                
        978 132 456
        645 897 123
        312 564 789
                
        483 276 591
        756 918 234
        129 345 867
        """);

    assertEquals(one, two);
    Assertions.assertNotEquals(one, three);
  }

  @Test
  void testMultipleSolutions() {
    final SudokuModel solver = new SudokuModel(3, 3);
    solver.loadFromText("""
          926 571 483
          351 486 279
          874 923 516
          
          582 367 194
          149 258 367
          763 1.. 825
          
          238 7.. 651
          617 835 942
          495 612 738
        """);

    var errors = solver.findErrors();
    System.out.println("Errors: " + errors);

    Set<SudokuModel> foundSolutions = solver.solve(Integer.MAX_VALUE);

    for (SudokuModel s : foundSolutions) {
      System.out.println("\n-----\n" + s.cellsAsText() + "\n-----\n");
    }

    System.out.print("Found " + foundSolutions.size());
  }

  @Test
  void testVegardHanssenPuzzle2155141() {
    assertOnlySolution(3, 3,
        """
                ... 6.. 4..
                7.. ..3 6..
                ... .91 .8.

                ... ... ...
                .5. 18. ..3
                ... 3.6 .45

                .4. 2.. .6.
                9.3 ... ...
                .2. ... 1..
            """, """
                581 672 439
                792 843 651
                364 591 782

                438 957 216
                256 184 973
                179 326 845

                845 219 367
                913 768 524
                627 435 198
            """
    );
  }

  @Test
  void testSudoku16x16() {
    assertOnlySolution(4, 4,
        """
            .F.1 .2AE C... ....
            .63G C084 EF10 2000
            E.97 B3F0 0000 0000
            4D2C 0000 6000 0F00

            .... E1B7 35A0 080C
            3G.. 2400 0E7D 005F
            B.5. ..... 94. .6..
            .... D.G5 F..C ....

            .... 9.1C .83A B.F.
            2C.B ..E3 54.. ..9.
            63.4 ..D. .B91 .CG2
            ..A9 .... ..C .8.67

            C8.. G..A .D.. .5..
            5... 3.46 .1F. ....
            .916 .E.B ..2. ..A8
            .E.. .D9. 4CB8 ..2.
                        """, """
            8FB1 62AE  C7D3 G945
            A63G C584  EF19 2B7D
            E597 B3FD  82G4 CA16
            4D2C 197G  6A5B 3F8E

            926F E1B7 35AG 48DC
            3GC8 2469 BE7D A15F
            BA5D 8C3F 1942 76EG
            147E DAG5 F68C 923B

             D7G5 961C 283A BEF4
             2C8B 7GE3 546F 1D9A
             63E4 AFD8 7B91 5CG2
             F1A9 4B52 DGCE 8367
             
            C843 G72A 9DE6 F5B1
            5BD2 3846 A1F7 EGC9
            7916 FECB G325 D4A8
            GEFA 5D91 4CB8 6723            
            """
    );
  }

  @Test
  void testSudokuArtoInkalaWorldsHardest() {
    assertOnlySolution(3, 3,
        """
                8.. ... ...
                ..3 6.. ...
                .7. .9. 2..

                .5. ..7 ...
                ... .45 7..
                ... 1.. .3.

                ..1 ... .68
                ..8 5.. .1.
                .9. ... 4..
            """, """
                812 753 649
                943 682 175
                675 491 283

                154 237 896
                369 845 721
                287 169 534

                521 974 368
                438 526 917
                796 318 452
            """
    );
  }


}