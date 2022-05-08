package ir;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Arrays;

public class EsUtil {

    private RestClient restClient;
    private ElasticsearchClient client;
    
    public EsUtil(){
        restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

        client = new ElasticsearchClient(transport);
    }

    public void testConn() throws IOException {
        System.out.println(client.info().tagline());
        if (!client.info().tagline().equals("You Know, for Search")){
            throw new IOException();
        }
    }

    public SearchResponse<DocEntry> fieldSearch(String name, int is_class, String super_types, String parameters, String return_type, String comments_and_fields) throws IOException {
        BoolQuery.Builder q = QueryBuilders.bool();
        // must have name
        Query byName = MatchQuery.of(m -> m // <1>
                .field("name")
                .query(name)
        )._toQuery();
        q.must(byName);

        if (is_class == 1) {
            Query byIsClass = MatchQuery.of(m -> m // <1>
                    .field("is_class")
                    .query(true)
            )._toQuery();
            q.must(byIsClass);
        } else if (is_class == 2) {
            Query byIsClass = MatchQuery.of(m -> m // <1>
                    .field("is_class")
                    .query(false)
            )._toQuery();
            q.must(byIsClass);
        }
        if (super_types != null && !super_types.equals("")) {
            Query bySuperTypes = MatchQuery.of(m -> m // <1>
                    .field("super_types")
                    .query(super_types)
            )._toQuery();
            q.must(bySuperTypes);
        }
        if (parameters != null && !parameters.equals("")) {
            Query byParameters = MatchQuery.of(m -> m // <1>
                    .field("parameters")
                    .query(parameters)
            )._toQuery();
            q.must(byParameters);
        }
        if (return_type != null && !return_type.equals("")) {
            Query byReturnType = MatchQuery.of(m -> m // <1>
                    .field("return_type")
                    .query(return_type)
            )._toQuery();
            q.must(byReturnType);
        }
        if (comments_and_fields != null && !comments_and_fields.equals("")) {
            Query byCommentsAndFields = MatchQuery.of(m -> m // <1>
                    .field("comments_and_fields")
                    .query(comments_and_fields)
            )._toQuery();
            q.must(byCommentsAndFields);
        }
        SearchResponse<DocEntry> res = client.search(s -> s
                        .index("github_search")
                        .query(q.build()._toQuery()
                        ).size(10000),
                DocEntry.class
        );

//        System.out.println(res.hits().total().value());
//        HashMap<DocEntry, Double> entryScore = new HashMap<>();
//        int cnt = 0;
//        for (Hit<DocEntry> hit: res.hits().hits()) {
//            entryScore.put(hit.source(), hit.score());
//            assert hit.source() != null;
//            cnt ++;
//            System.out.println(hit.source().name);
//            System.out.println(hit.source().file_name);
//        }
//        System.out.println(cnt);
        return res;
    }

    public SearchResponse<DocEntry> keywordSearch(String keyword) throws IOException {
        MultiMatchQuery.Builder q = QueryBuilders.multiMatch();
        q.query(keyword);
        SearchResponse<DocEntry> res = client.search(s -> s
                        .index("github_search")
                        .query(q.build()._toQuery()
                        ).size(10000),
                DocEntry.class
        );
//        System.out.println(res.hits().total().value());
//        for (Hit<DocEntry> hit: res.hits().hits()) {
//            System.out.println(hit.source().name);
//            System.out.println(hit.score());
//        }
        return res;
    }
    public SearchResponse<DocEntry> keywordSearchWithFieldBoost(String keyword, int boostNameCoeff, int boostSuperTypeCoeff, int boostParamCoeff, int boostReturnCoeff, int boostCnFCoeff) throws IOException {
        MultiMatchQuery.Builder q = QueryBuilders.multiMatch();
        String[] fields = {"name^" + String.valueOf(boostNameCoeff), "super_types^" + String.valueOf(boostSuperTypeCoeff), "parameters^" + String.valueOf(boostParamCoeff), "return_type^" + String.valueOf(boostReturnCoeff), "comments_and_fields^" + String.valueOf(boostCnFCoeff)};
        q.query(keyword);
        q.fields(Arrays.asList(fields));
        SearchResponse<DocEntry> res = client.search(s -> s
                        .index("github_search")
                        .query(q.build()._toQuery()
                        ).size(10000),
                DocEntry.class
        );
//        System.out.println(res.hits().total().value());
//        for (Hit<DocEntry> hit: res.hits().hits()) {
//            System.out.println(hit.source().name);
//            System.out.println(hit.score());
//        }
        return res;
    }

    public SearchResponse<DocEntry> fuzzySearch(String name, int is_class, String super_types, String parameters, String return_type, String comments_and_fields) throws IOException {
        FuzzyQuery.Builder q = QueryBuilders.fuzzy();
        q.field("name").value(name);
        if (super_types != null && !super_types.equals("")) {
            q.field("super_types").value(super_types);
        }
        if (parameters != null && !parameters.equals("")) {
            q.field("parameters").value(parameters);
        }
        if (return_type != null && !return_type.equals("")) {
            q.field("return_type").value(return_type);
        }
        if (comments_and_fields != null && !comments_and_fields.equals("")) {
            q.field("comments_and_fields").value(comments_and_fields);
        }

        SearchResponse<DocEntry> res = client.search(s -> s
                        .index("github_search")
                        .query(q.build()._toQuery()
                        ).size(10000),
                DocEntry.class
        );
//        System.out.println(res.hits().total().value());
//        for (Hit<DocEntry> hit: res.hits().hits()) {
//            System.out.println(hit.source().name);
//            System.out.println(hit.score());
//        }
        return res;
    }

    public SearchResponse<DocEntry> keywordSearchWithIsClass(String keyword, int is_class) throws IOException {
        BoolQuery.Builder q = QueryBuilders.bool();
        if (is_class == 1) {
            Query byIsClass = MatchQuery.of(m -> m // <1>
                    .field("is_class")
                    .query(true)
            )._toQuery();
            q.must(byIsClass);
        } else if (is_class == 2) {
            Query byIsClass = MatchQuery.of(m -> m // <1>
                    .field("is_class")
                    .query(false)
            )._toQuery();
            q.must(byIsClass);
        }
        MultiMatchQuery.Builder multiMatch = QueryBuilders.multiMatch();
        multiMatch.query(keyword);
        q.must(multiMatch.build()._toQuery());
        SearchResponse<DocEntry> res = client.search(s -> s
                        .index("github_search")
                        .query(q.build()._toQuery()
                        ).size(10000),
                DocEntry.class
        );
//        System.out.println(res.hits().total().value());
//        for (Hit<DocEntry> hit: res.hits().hits()) {
//            System.out.println(hit.source().name);
//            System.out.println(hit.score());
//        }
        return res;
    }

    public static void main( String[] args ) {
        EsUtil test = new EsUtil();
        try {
            test.testConn();
//            test.fieldSearch("getValue", 0, "Comparable", "", "", "");
            test.keywordSearch("getValue");
//            test.keywordSearchWithFieldBoost("getValue", 1,1,1,1,1);
            test.keywordSearchWithIsClass("getValue", 2);
//            test.fuzzySearch("getValue", 0, "Comparable", "", "", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
