package ao.co.isptec.aplm.binasjc.retrofit;

import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Estacao;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EstacaoApi {

    @GET("/Estacao/get-all")
    Call<List<Estacao>> getAllEstacaoes();

    @GET("/Estacao/getEstacaoByNome")
    Call<List<Estacao>> getEstacaoByNome(@Query("nome") String nome);

    @GET("/Estacao/getEstacaoByID")
    Call<Estacao> getEstacaoByID(@Query("id") int id);

    @POST("/Estacao/save")
    Call<Estacao> save(@Body Estacao estacao);

    @DELETE("/Estacao/delete")
    Call<Void> delete(@Body Estacao estacao);


}
