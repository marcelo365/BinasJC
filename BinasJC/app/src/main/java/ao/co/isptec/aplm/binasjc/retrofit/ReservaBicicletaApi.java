package ao.co.isptec.aplm.binasjc.retrofit;

import java.util.List;

import ao.co.isptec.aplm.binasjc.model.Estacao;
import ao.co.isptec.aplm.binasjc.model.ReservaBicicleta;
import ao.co.isptec.aplm.binasjc.model.Utilizador;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ReservaBicicletaApi {

    @GET("/ReservaBicicleta/get-all")
    Call<List<ReservaBicicleta>> getAllReservasBicicletas();

    @GET("/ReservaBicicleta/getReservaBicicletaByUsuarioAndEstado")
    Call<List<ReservaBicicleta>> getReservaBicicletaByUsuarioAndEstado( @Query("idUsuario") int idUsuario, @Query("estado") int estado);

    @POST("/ReservaBicicleta/save")
    Call<ReservaBicicleta> save(@Body ReservaBicicleta reservaBicicleta);

    @DELETE("/ReservaBicicleta/delete")
    Call<Void> delete(@Body ReservaBicicleta reservaBicicleta);


}
