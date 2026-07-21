package com.apocalypse.caerulaarbor.analysis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Events包分析器
 * 分析events目录下所有事件处理类，识别哪些类使用同一个事件
 * 并输出详细的分析文档
 */
public class EventsAnalyzer {

    private static final String EVENTS_DIR = "src/main/java/com/apocalypse/caerulaarbor/event";
    private static final String OUTPUT_FILE = "src/test/java/com/apocalypse/caerulaarbor/analysis/EventsAnalysisReport.md";

    private static final Pattern EVENT_BUS_SUBSCRIBER = Pattern.compile("@SubscribeEvent");
    private static final Pattern EVENT_METHOD = Pattern.compile("public\\s+static\\s+void\\s+\\w+\\s*\\(([^)]+)\\s+event\\)");
    private static final Pattern EVENT_TYPE = Pattern.compile("public\\s+static\\s+void\\s+\\w+\\s*\\([^)]+\\.(\\w+)\\s+event\\)");
    private static final Pattern CLASS_NAME = Pattern.compile("public\\s+class\\s+(\\w+)");
    private static final Pattern PRIORITY = Pattern.compile("@SubscribeEvent\\s*\\(\\s*priority\\s*=\\s*EventPriority\\.(\\w+)\\s*\\)");

    public static void main(String[] args) {
        try {
            EventsAnalyzer analyzer = new EventsAnalyzer();
            analyzer.analyzeAndGenerateReport();
            System.out.println("分析完成！报告已生成: " + OUTPUT_FILE);
        } catch (Exception e) {
            System.err.println("分析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void analyzeAndGenerateReport() throws IOException {
        Path eventsPath = Paths.get(EVENTS_DIR);
        if (!Files.exists(eventsPath)) {
            throw new IOException("events目录不存在: " + EVENTS_DIR);
        }

        Map<String, List<EventClassInfo>> eventGroups = new LinkedHashMap<>();
        List<EventClassInfo> allClasses = new ArrayList<>();

        try (Stream<Path> stream = Files.list(eventsPath)) {
            List<Path> javaFiles = stream
                    .filter(p -> p.toString().endsWith(".java"))
                    .sorted()
                    .collect(Collectors.toList());

            for (Path javaFile : javaFiles) {
                EventClassInfo classInfo = analyzeFile(javaFile);
                if (classInfo != null && classInfo.eventType != null) {
                    allClasses.add(classInfo);
                    eventGroups.computeIfAbsent(classInfo.eventType, k -> new ArrayList<>()).add(classInfo);
                }
            }
        }

        generateReport(eventGroups, allClasses);
    }

    private EventClassInfo analyzeFile(Path filePath) throws IOException {
        String content = readFileContent(filePath);
        String fileName = filePath.getFileName().toString();
        String className = fileName.replace(".java", "");

        Matcher methodMatcher = EVENT_METHOD.matcher(content);
        Matcher typeMatcher = EVENT_TYPE.matcher(content);
        Matcher priorityMatcher = PRIORITY.matcher(content);

        String eventType = null;
        String methodName = null;
        String fullEventType = null;
        String priority = "NORMAL";

        if (typeMatcher.find()) {
            eventType = typeMatcher.group(1);
            fullEventType = extractFullEventType(content);
        }

        if (methodMatcher.find()) {
            methodName = extractMethodName(content);
        }

        if (priorityMatcher.find()) {
            priority = priorityMatcher.group(1);
        }

        if (eventType == null) {
            return null;
        }

        int subscribeCount = countSubscribeEvents(content);
        String description = extractDescription(content, className);

        return new EventClassInfo(className, fileName, eventType, fullEventType, methodName, priority, subscribeCount, description);
    }

    private String extractFullEventType(String content) {
        Pattern pattern = Pattern.compile("\\w+\\.(\\w+)\\s+event\\)");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String shortName = matcher.group(1);
            return resolveFullEventName(shortName);
        }
        return null;
    }

    private String resolveFullEventName(String shortName) {
        Map<String, String> eventMapping = new HashMap<>();
        eventMapping.put("PlayerTickEvent", "TickEvent.PlayerTickEvent");
        eventMapping.put("LivingTickEvent", "LivingEvent.LivingTickEvent");
        eventMapping.put("LivingAttackEvent", "LivingAttackEvent");
        eventMapping.put("LivingHurtEvent", "LivingHurtEvent");
        eventMapping.put("LivingDeathEvent", "LivingDeathEvent");
        eventMapping.put("LivingHealEvent", "LivingHealEvent");
        eventMapping.put("LivingEntityUseItemEvent.Finish", "LivingEntityUseItemEvent.Finish");
        eventMapping.put("EntityInteract", "PlayerInteractEvent.EntityInteract");
        eventMapping.put("LeftClickEmpty", "PlayerInteractEvent.LeftClickEmpty");
        eventMapping.put("PlayerLoggedInEvent", "PlayerEvent.PlayerLoggedInEvent");
        eventMapping.put("PlayerRespawnEvent", "PlayerEvent.PlayerRespawnEvent");
        eventMapping.put("ItemCraftedEvent", "PlayerEvent.ItemCraftedEvent");
        eventMapping.put("EntityPlaceEvent", "BlockEvent.EntityPlaceEvent");
        eventMapping.put("EntityLeaveLevelEvent", "EntityLeaveLevelEvent");
        eventMapping.put("CriticalHitEvent", "CriticalHitEvent");
        eventMapping.put("PlayerWakeUpEvent", "PlayerWakeUpEvent");
        eventMapping.put("BlockBreakEvent", "BlockEvent.BreakEvent");
        return eventMapping.getOrDefault(shortName, shortName);
    }

    private String extractMethodName(String content) {
        Pattern pattern = Pattern.compile("public\\s+static\\s+void\\s+(\\w+)\\s*\\(");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private int countSubscribeEvents(String content) {
        Pattern pattern = Pattern.compile("@SubscribeEvent");
        Matcher matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private String extractDescription(String content, String className) {
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("// TODO:") || line.startsWith("// 这是一个事件处理器")) {
                return line.replace("// TODO:", "").replace("//", "").trim();
            }
        }
        return "";
    }

    private void generateReport(Map<String, List<EventClassInfo>> eventGroups, List<EventClassInfo> allClasses) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("# Events 包事件处理器分析报告\n\n");
        sb.append("> 自动生成时间: ").append(java.time.LocalDateTime.now()).append("\n\n");
        sb.append("---\n\n");

        sb.append("## 概览\n\n");
        sb.append("- **总事件处理类数量**: ").append(allClasses.size()).append("\n");
        sb.append("- **事件类型数量**: ").append(eventGroups.size()).append("\n\n");

        sb.append("---\n\n");

        sb.append("## 按事件类型分组\n\n");
        sb.append("以下表格展示了哪些类使用的是同一个事件：\n\n");

        int groupIndex = 1;
        for (Map.Entry<String, List<EventClassInfo>> entry : eventGroups.entrySet()) {
            String eventType = entry.getKey();
            List<EventClassInfo> classes = entry.getValue();

            sb.append("### ").append(groupIndex).append(". ").append(eventType).append(" (").append(classes.size()).append(" 个类)\n\n");

            String fullEventType = classes.get(0).fullEventType;
            if (fullEventType != null) {
                sb.append("**完整事件类型**: `").append(fullEventType).append("`\n\n");
            }

            sb.append("| 序号 | 类名 | 方法名 | 优先级 | @SubscribeEvent数量 |\n");
            sb.append("|------|------|--------|--------|-------------------|\n");

            for (int i = 0; i < classes.size(); i++) {
                EventClassInfo info = classes.get(i);
                sb.append("| ").append(i + 1)
                  .append(" | `").append(info.className).append("`")
                  .append(" | `").append(info.methodName != null ? info.methodName : "N/A").append("`")
                  .append(" | ").append(info.priority)
                  .append(" | ").append(info.subscribeCount)
                  .append(" |\n");
            }

            sb.append("\n");

            if (classes.size() > 1) {
                sb.append("**说明**: 以上 ").append(classes.size()).append(" 个类都监听同一个事件 `").append(eventType).append("`\n\n");
            }

            sb.append("---\n\n");
            groupIndex++;
        }

        sb.append("## 事件类型统计\n\n");
        sb.append("| 事件类型 | 类数量 | 占比 |\n");
        sb.append("|----------|--------|------|\n");

        for (Map.Entry<String, List<EventClassInfo>> entry : eventGroups.entrySet()) {
            int count = entry.getValue().size();
            double percentage = (double) count / allClasses.size() * 100;
            sb.append("| `").append(entry.getKey()).append("` | ")
              .append(count).append(" | ")
              .append(String.format("%.1f%%", percentage)).append(" |\n");
        }

        sb.append("\n---\n\n");

        sb.append("## 完整类列表\n\n");
        sb.append("| 序号 | 类名 | 事件类型 | 优先级 | 说明 |\n");
        sb.append("|------|------|----------|--------|------|\n");

        for (int i = 0; i < allClasses.size(); i++) {
            EventClassInfo info = allClasses.get(i);
            sb.append("| ").append(i + 1)
              .append(" | `").append(info.className).append("`")
              .append(" | ").append(info.eventType)
              .append(" | ").append(info.priority)
              .append(" | ").append(info.description != null && !info.description.isEmpty() ? info.description : "-")
              .append(" |\n");
        }

        sb.append("\n---\n\n");

        sb.append("## 使用同一事件的类组详细分析\n\n");

        int sectionIndex = 1;
        for (Map.Entry<String, List<EventClassInfo>> entry : eventGroups.entrySet()) {
            if (entry.getValue().size() < 2) {
                continue;
            }

            String eventType = entry.getKey();
            List<EventClassInfo> classes = entry.getValue();

            sb.append("### 组 ").append(sectionIndex).append(": ").append(eventType).append("\n\n");
            sb.append("以下类都使用 `").append(eventType).append("` 事件：\n\n");

            for (EventClassInfo info : classes) {
                sb.append("- **").append(info.className).append("**\n");
                if (info.description != null && !info.description.isEmpty()) {
                    sb.append("  - 说明: ").append(info.description).append("\n");
                }
            }

            sb.append("\n");
            sectionIndex++;
        }

        writeToFile(OUTPUT_FILE, sb.toString());
    }

    private String readFileContent(Path filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private void writeToFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }

    static class EventClassInfo {
        String className;
        String fileName;
        String eventType;
        String fullEventType;
        String methodName;
        String priority;
        int subscribeCount;
        String description;

        EventClassInfo(String className, String fileName, String eventType, String fullEventType, 
                      String methodName, String priority, int subscribeCount, String description) {
            this.className = className;
            this.fileName = fileName;
            this.eventType = eventType;
            this.fullEventType = fullEventType;
            this.methodName = methodName;
            this.priority = priority;
            this.subscribeCount = subscribeCount;
            this.description = description;
        }
    }
}
