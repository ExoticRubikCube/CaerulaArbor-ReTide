import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ResourceLocationUpdater {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("请输入要搜索的目录路径:");
        String directoryPath = scanner.nextLine().trim();

        Path startPath = Paths.get(directoryPath);

        if (!Files.exists(startPath) || !Files.isDirectory(startPath)) {
            System.err.println("错误: 路径无效或不是文件夹。");
            return;
        }

        System.out.println("开始扫描...");
        try {
            long count = Files.walk(startPath)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .mapToInt(ResourceLocationUpdater::processFile)
                    .sum();
            System.out.println("完成! 共修改了 " + count + " 个文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    private static int processFile(Path path) {
        try {
            // 读取文件（指定UTF-8编码，防止中文注释乱码）
            String content = Files.readString(path, StandardCharsets.UTF_8);
            
            // 执行替换
            String newContent = content.replace(
                    "new ResourceLocation(", 
                    "ResourceLocation.fromNamespaceAndPath("
            );
            
            // 检查是否有改动
            if (!content.equals(newContent)) {
                Files.writeString(path, newContent, StandardCharsets.UTF_8);
                System.out.println("已修改: " + path);
                return 1;
            }
        } catch (IOException e) {
            System.err.println("处理失败: " + path);
        }
        return 0;
    }
}