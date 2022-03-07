package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 并未实现，因为我感觉这个算法没屌用，很容易绕过。。
 */
@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败！" + e.getMessage());
        }

    }

    private void addKeyword(String keyword) {
        TrieNode tempNode = root;
        for (int i = 0; i < keyword.length(); ++i) {
            Character ch = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(ch);
            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(ch, subNode);
            }
            tempNode = subNode;
        }
        tempNode.setKeywordEnd(true);
    }


    /**
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        return text;
    }

    private class TrieNode {
        private boolean isKeywordEnd = false;
        private Map<Character, TrieNode> subnodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TrieNode node) {
            subnodes.put(c, node);
        }

        public TrieNode getSubNode(Character character) {
            return subnodes.get(character);
        }
    }
}
