package dev.xeonkryptos.xeonrobotframeworkplugin.ide;

import com.intellij.codeInsight.TailType;
import com.intellij.openapi.editor.Editor;

public final class RobotTailTypes {

   public static final TailType NEW_LINE = TailType.createSimpleTailType('\n');
   public static final TailType TAB = new TailType() {
      @Override
      public int processTail(Editor editor, int tailOffset) {
         editor.getDocument().insertString(tailOffset, "    ");
         return moveCaret(editor, tailOffset, 4);
      }
   };
}
