package UpSolverStorage;

import org.thymeleaf.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class StorageImpl implements Storage {

    private static Map<String, UpSolverNode> prefixPathToStorageMap = new ConcurrentHashMap<>();

    @Override
    public void write(String fullPath, Content content) {
        //TODO impl
        validatePath(fullPath);
        String[] split = StringUtils.split(fullPath, "\\");

//        UpSolverNode upSolverNode = prefixPathToStorageMap.computeIfAbsent(split[0], e -> new UpSolverNode(split[0]));
        UpSolverNode upSolverRootNode = prefixPathToStorageMap.get(split[0]);
        UpSolverNode prevNode = null;
        if (upSolverRootNode == null) {
            //TODO create it with children
            for (int i = 0; i < split.length; i++) {
                Content content1;
                if (i < split.length - 1 || content instanceof Directory) {
                    content1 = new Directory(split[i]);
                } else {
                    content1 = content;
                }

                if (i == 0 && i < split.length - 1) {
                    upSolverRootNode = new UpSolverNode(content1, null);
                    prefixPathToStorageMap.put(split[0], upSolverRootNode);
                } else {
                    upSolverRootNode = new UpSolverNode(content1, prevNode);
                    prevNode.getChildren().put(split[i], upSolverRootNode);
                }
                prevNode = upSolverRootNode;
            }

        } else {
            //TODO search
            UpSolverNode prev = upSolverRootNode;
            for (int i = 1; i < split.length; i++) {
                Map<String, UpSolverNode> children = prev.getChildren();
                String s = split[i];
                UpSolverNode upSolverRoot = children.get(s);
                Content content1;
                if (upSolverRoot == null) {
                    //TODO create it
                    if (i < split.length - 1 || content instanceof Directory) {
                        content1 = new Directory(split[i]);
                    } else {
                        content1 = content;
                    }
                    upSolverRoot = new UpSolverNode(content1, prev);
                    upSolverRoot.setContent(content1);
                    prevNode.getChildren().put(split[i], upSolverRoot);
                } else {
                    //TODO override
                    if (i == split.length - 1) {
                        if (content.name.equals(split[i]) && content instanceof File) {
                            //TODO override
                            prevNode.setContent(content);
                        } else {
                            //TODO create new
                            if (content instanceof Directory) {
                                content1 = new Directory(split[i]);
                            } else {
                                content1 = content;
                            }
                            upSolverRoot = new UpSolverNode(content1, prev);
                            upSolverRoot.setContent(content1);
                            prevNode.getChildren().put(split[i], upSolverRoot);

                        }
                    }
                }
                prevNode = upSolverRoot;
            }
        }

    }

    @Override
    public Content read(String fullPath) {
        //TODO impl
        validatePath(fullPath);
        String[] split = StringUtils.split(fullPath, "\\");
        UpSolverNode upSolverNode = prefixPathToStorageMap.get(split[0]);
        if (upSolverNode == null) {
            throw new NoSuchElementException();
        }

        for (int i = 1; i < split.length && upSolverNode.getChildren() != null; i++) {
            String s = split[i];
            upSolverNode = upSolverNode.getChildren().get(s);
        }
        if (upSolverNode != null) {
            return upSolverNode.getContent();
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<Content> list(String fullPath) {
        //TODO impl
        validatePath(fullPath);

        String[] split = StringUtils.split(fullPath, "\\");
        UpSolverNode upSolverNode = prefixPathToStorageMap.get(split[0]);
        if (upSolverNode == null) {
            throw new NoSuchElementException();
        }

        for (int i = 1; i < split.length && upSolverNode.getChildren() != null; i++) {
            String s = split[i];
            upSolverNode = upSolverNode.getChildren().get(s);
        }
        if (upSolverNode != null) {
            Map<String, UpSolverNode> children = upSolverNode.getChildren();
            return children.values().stream().map(UpSolverNode::getContent).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Content> listRecursively(String fullPath) {
        //TODO impl
        validatePath(fullPath);
        String[] split = StringUtils.split(fullPath, "\\");
        UpSolverNode upSolverNode = prefixPathToStorageMap.get(split[0]);
        if (upSolverNode == null) {
            throw new NoSuchElementException();
        }

        for (int i = 1; i < split.length && upSolverNode.getChildren() != null; i++) {
            String s = split[i];
            upSolverNode = upSolverNode.getChildren().get(s);
        }

        if (upSolverNode != null) {
            List<Content> contents = new ArrayList<>();
            addContent(upSolverNode.getChildren(), contents);
            return contents;
        }

        return Collections.emptyList();
    }

    private void addContent(Map<String, UpSolverNode> upSolverNodeMap, List<Content> result) {
        for (Map.Entry<String, UpSolverNode> entry : upSolverNodeMap.entrySet()) {
            UpSolverNode upSolverNode = entry.getValue();
            result.add(upSolverNode.getContent());
            addContent(upSolverNode.getChildren(), result);
        }
    }

    private void validatePath(String fullPath) {
        if (StringUtils.isEmpty(fullPath))
            throw new IllegalArgumentException();
    }
}
