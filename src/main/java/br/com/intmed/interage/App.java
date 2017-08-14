package br.com.intmed.interage;

import br.com.intmed.interage.resources.Interacao;
import br.com.intmed.interage.results.InteracaoResults;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Exemplo em Java do consumo do serviço de interações medicamentosas Interage
 * usando Unirest (http://unirest.io/java.html)
 *
 * Exibindo todas as interações medicamentosas existentes entre os
 * princípios ativos: Lorazepam, Ranitidine e Granisetrona
 */
public class App 
{
    public static void main( String[] args )
    {
        // Registrando ObjectMapper (Apenas uma vez)
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        try {
            String token = "your-api-token";
            String protocolo = "https";
            String host = "api.interage.intmed.com.br";
            String endpoint = "/v1/interacoes/?principios_ativos=17&principios_ativos=443&principios_ativos=681";
            URL url = new URL(protocolo, host, endpoint);

            // Requisitando endpoint
            HttpResponse<InteracaoResults> interacaoResultsHttpResponse = Unirest.get(url.toString())
                    .header("accept", "application/json")
                    .header("Authorization", "Token " + token)
                    .asObject(InteracaoResults.class);
            InteracaoResults interacaoResults = interacaoResultsHttpResponse.getBody();

            int count = 1;
            for(Interacao interacao : interacaoResults.getResults()) {
                System.out.println("# INTERAÇÃO " + count);
                System.out.println(String.format("%s <-> %s", interacao.getPrincipioAtivos().get(0).getNome(), interacao.getPrincipioAtivos().get(1).getNome()));
                System.out.println(String.format("Gravidade: %s", interacao.getGravidade().toString()));
                System.out.println(String.format("Evidência: %s", interacao.getEvidencia().toString()));
                System.out.println(String.format("Ação: %s", interacao.getAcao().toString()));
                System.out.println("\n");
                count++;
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
