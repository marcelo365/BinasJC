package ao.co.isptec.aplm.binasjc.retrofit;

import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Utilizador;
import kotlin.ParameterName;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UtilizadorApi {

    @GET("/Utilizador/get-all")
    Call<List<Utilizador>> getAllUtilizadores();

    @GET("/Utilizador/getUtilizadorByUsername")
    Call<Utilizador> getUtilizadorByUsername(@Query("username") String username);

    @GET("/Utilizador/getUtilizadorByUsernameAndSenha")
    Call<Utilizador> getUtilizadorByUsernameAndSenha(@Query("username") String username , @Query("senha") String senha);

    @POST("/Utilizador/save")
    Call<Utilizador> save (@Body Utilizador utilizador);

    @DELETE("/Utilizador/delete")
    Call<Void> delete (@Body Utilizador utilizador);

}
