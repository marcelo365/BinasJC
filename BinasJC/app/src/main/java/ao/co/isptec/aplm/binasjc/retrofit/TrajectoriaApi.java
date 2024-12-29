package ao.co.isptec.aplm.binasjc.retrofit;

import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Estacao;
import ao.co.isptec.aplm.binasjc.model.Trajectoria;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TrajectoriaApi {

    @POST("/Trajectoria/save")
    Call<Trajectoria> save(@Body Trajectoria trajectoria);

    @GET("/Trajectoria/getTrajectoriaByIdUsuario")
    Call<List<Trajectoria>> getTrajectoriaByIdUsuario(@Query("idUsuario") int idUsuario);

}
