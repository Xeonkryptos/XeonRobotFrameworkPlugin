package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTFactory;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ParsingDiagnostics;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;

public class RobotStubFileElementType extends IStubFileElementType<PsiFileStub<?>> {

    public RobotStubFileElementType() {
        super("ROBOT_FILE", RobotLanguage.INSTANCE);
    }

    @Override
    public int getStubVersion() {
        return super.getStubVersion() + 22;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        Project project = psi.getProject();
        Language languageForParser = getLanguageForParser(psi);

        ParserDefinition parserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(RobotLanguage.INSTANCE);
        PsiBuilder builder = new PsiBuilderImpl(project, parserDefinition, parserDefinition.createLexer(project), chameleon, chameleon.getChars()) {
            @Override
            protected @NotNull TreeElement createLeaf(@NotNull IElementType type, int start, int end) {
                if (type == RobotTypes.CONTINUATION) {
                    CharSequence text = getInternedText(start, end);
                    return ASTFactory.leaf(type, text);
                }
                return super.createLeaf(type, start, end);
            }
        };
        PsiParser parser = LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser).createParser(project);

        long startTime = System.nanoTime();
        ASTNode node = parser.parse(this, builder);
        ParsingDiagnostics.registerParse(builder, languageForParser, System.nanoTime() - startTime);

        return node.getFirstChildNode();
    }
}
