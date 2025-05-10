package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotReportsFilter implements Filter, PossiblyDumbAware, DumbAware {

    private static final String WINDOWS_PATH_REGEX = "[a-zA-Z]:\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*|\\\\\\\\[^\\\\/:*?\"<>|\\r\\n]+\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*";
    private static final String UNIX_PATH_REGEX = "/(?:[^/\\x00\\s:*?\"<>|]+/)*[^/\\x00\\s:*?\"<>|]*|~(?:/[^/\\x00\\s:*?\"<>|]+)*";

    private static final Pattern WINDOWS_PATH_PATTERN = Pattern.compile(WINDOWS_PATH_REGEX);
    private static final Pattern UNIX_PATH_PATTERN = Pattern.compile(UNIX_PATH_REGEX);

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("html", "htm", "xml");

    @Nullable
    @Override
    public Result applyFilter(@NotNull String line, int entireLength) {
        List<ResultItem> resultItems = new ArrayList<>();
        int startOffset = entireLength - line.length();

        Pattern pattern = SystemInfo.isWindows ? WINDOWS_PATH_PATTERN : UNIX_PATH_PATTERN;
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String path = matcher.group();
            if (!path.isEmpty()) {
                String fileName = new File(path).getName();
                int fileExtensionStartIndex = fileName.lastIndexOf('.');
                if (fileExtensionStartIndex == -1) {
                    continue; // No file extension found
                }
                String fileExtension = fileName.substring(fileExtensionStartIndex + 1).toLowerCase();
                if (!SUPPORTED_EXTENSIONS.contains(fileExtension)) {
                    continue; // Unsupported file extension
                }

                int highlightStartOffset = startOffset + matcher.start();
                int highlightEndOffset = startOffset + matcher.end();

                HyperlinkInfo hyperlinkInfo = project -> {
                    URI fileUri = new File(path).toURI();
                    BrowserUtil.browse(fileUri);
                };

                resultItems.add(new ResultItem(highlightStartOffset, highlightEndOffset, hyperlinkInfo, null));
            }
        }
        return resultItems.isEmpty() ? null : new Result(resultItems);
    }
}
