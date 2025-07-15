//package es.lucene;
//
////import org.apache.lucene.analysis.Analyzer;
//
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.*;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
//import org.apache.lucene.search.*;
//import org.apache.lucene.store.Directory;
//
//public class LuceneComplexDemo {
//
//    public static void main(String[] args) throws Exception {
//        // 1. 创建内存索引
//        Directory directory = new RAMDirectory();
//        Analyzer analyzer = new StandardAnalyzer();
//        IndexWriterConfig config = new IndexWriterConfig(analyzer);
//        IndexWriter writer = new IndexWriter(directory, config);
//
//        // 2. 添加多条文档
//        addDoc(writer, "1", "Lucene Introduction", "Lucene is a powerful search library", 2020);
//        addDoc(writer, "2", "Elasticsearch Guide", "Elasticsearch is built on Lucene", 2019);
//        addDoc(writer, "3", "Java Programming", "Learn Java for backend development", 2021);
//        addDoc(writer, "4", "Lucene vs Elasticsearch", "Comparing Lucene and Elasticsearch", 2022);
//        addDoc(writer, "5", "Advanced Search Techniques", "Boost your search with Lucene", 2023);
//
//        writer.close();
//
//        // 3. 创建搜索器
//        DirectoryReader reader = DirectoryReader.open(directory);
//        IndexSearcher searcher = new IndexSearcher(reader);
//
//        // 4. 多字段查询示例 - 搜索标题和内容中包含 "Lucene" 或 "Elasticsearch"
//        String[] fields = {"title", "content"};
//        MultiFieldQueryParser multiParser = new MultiFieldQueryParser(fields, analyzer);
//        Query multiFieldQuery = multiParser.parse("Lucene OR Elasticsearch");
//        printResults(searcher, multiFieldQuery, "多字段查询：title/content 包含 'Lucene' 或 'Elasticsearch'");
//
//        // 5. 布尔查询示例 - 标题包含 "Lucene" 且年份大于 2020
//        Query titleQuery = new TermQuery(new Term("title", "lucene"));
//        Query yearQuery = IntPoint.newRangeQuery("year", 2021, Integer.MAX_VALUE);
//
//        BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
//        boolQueryBuilder.add(titleQuery, BooleanClause.Occur.MUST);
//        boolQueryBuilder.add(yearQuery, BooleanClause.Occur.MUST);
//        Query boolQuery = boolQueryBuilder.build();
//        printResults(searcher, boolQuery, "布尔查询：标题包含 'Lucene' 且年份 >= 2021");
//
//        // 6. 短语查询示例 - 标题中精确包含短语 "Advanced Search"
//        PhraseQuery phraseQuery = new PhraseQuery("title", "advanced", "search");
//        printResults(searcher, phraseQuery, "短语查询：标题包含短语 'Advanced Search'");
//
//        // 7. 范围查询示例 - 年份在 2019 到 2022 之间
//        Query rangeQuery = IntPoint.newRangeQuery("year", 2019, 2022);
//        printResults(searcher, rangeQuery, "范围查询：年份在 2019 到 2022 之间");
//
//        reader.close();
//        directory.close();
//    }
//
//    // 工具：添加文档
//    private static void addDoc(IndexWriter writer, String id, String title, String content, int year) throws Exception {
//        Document doc = new Document();
//        doc.add(new StringField("id", id, Field.Store.YES));
//        doc.add(new TextField("title", title, Field.Store.YES));
//        doc.add(new TextField("content", content, Field.Store.NO)); // 内容字段不存储，节省空间
//        doc.add(new IntPoint("year", year)); // 数字类型字段，用于范围查询
//        doc.add(new StoredField("year", year)); // 存储年份，方便结果展示
//        writer.addDocument(doc);
//    }
//
//    // 工具：打印查询结果
//    private static void printResults(IndexSearcher searcher, Query query, String title) throws Exception {
//        System.out.println("=== " + title + " ===");
//        TopDocs topDocs = searcher.search(query, 10);
//        System.out.println("命中总数：" + topDocs.totalHits.value);
//        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
//            Document doc = searcher.doc(scoreDoc.doc);
//            System.out.printf("ID: %s, Title: %s, Year: %s, Score: %.4f%n",
//                    doc.get("id"), doc.get("title"), doc.get("year"), scoreDoc.score);
//        }
//        System.out.println();
//    }
//}
