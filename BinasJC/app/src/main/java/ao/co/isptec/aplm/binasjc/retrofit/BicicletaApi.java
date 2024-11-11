package ao.co.isptec.aplm.binasjc.retrofit;

import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Bicicleta;
import ao.co.isptec.aplm.binasjc.model.Estacao;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BicicletaApi {

    @GET("/Bicicleta/get-all")
    Call<List<Bicicleta>> getAllBicicletas();

    @GET("/Bicicleta/getBicicletaByNome")
    Call<List<Bicicleta>> getBicicletaByNome(@Query("nome") String nome);

    @GET("/Bicicleta/getBicicletaByID")
    Call<Bicicleta> getBicicletaByID(@Query("id") int id);

    @GET("/Bicicleta/getBicicletaByEstacao")
    Call<List<Bicicleta>> getBicicletaByEstacao(@Body Estacao estacao);

    @GET("/Bicicleta/getBicicletasDisponiveisEstacao")
    Call<List<Bicicleta>> getBicicletasDisponiveisEstacao(@Body Estacao estacao);

    @POST("/Bicicleta/save")
    Call<Bicicleta> save(@Body Bicicleta bicicleta);

    @DELETE("/Bicicleta/delete")
    Call<Void> delete(@Body Bicicleta bicicleta);
}
