package bbdd;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ConsultasApus {
	private Connection conexion;

	public ConsultasApus(Connection conexion) {
		this.conexion = conexion;
	}

	public int buscarIdUsuario(String usuario) throws SQLException {
		String consulta = "select id from usuario where nombre_usuario = \"" + usuario + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getInt(1) : null;
	}

	public String buscarCorreoUsuario(String correo) throws SQLException {
		String consulta = "select correo_electronico from usuario where correo_electronico = \"" + correo + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1) : null;
	}

	public String buscarNombreUsuario(String usuario) throws SQLException {
		String consulta = "select nombre_usuario from usuario where nombre_usuario = \"" + usuario + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1) : null;
	}

	public void crearUsuario(String correo, String usuario, String contrasenna, String nombre, String imagen)
			throws SQLException {
		String consulta = "insert into usuario(correo_electronico, nombre_usuario, contrasenna, nombre, imagen) "
				+ "values(?,?,?,?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, correo);
		ps.setString(2, usuario);
		ps.setString(3, contrasenna);
		ps.setString(4, nombre);
		ps.setString(5, imagen);

		ps.executeUpdate();
		System.out.println("Usuario creado con éxito :)");
	}

	public boolean accederUsuario(String usuario, String constrasenna) throws SQLException {
		if (buscarNombreUsuario(usuario) == null) {
			return false;
		}

		String consulta = "select contrasenna from usuario where nombre_usuario = \"" + usuario + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getString(1).equals(constrasenna) : null;
	}

	public boolean mostrarViajesUsuario(String usuario) throws SQLException {
		String consulta = "select viaje.* from usuario join usuario_viaje on usuario.id=usuario_viaje.id_usuario "
				+ "join viaje on usuario_viaje.id_viaje=viaje.id " + "where usuario.nombre_usuario = \"" + usuario
				+ "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();
		ResultSetMetaData rmd = res.getMetaData();

		if (!res.next()) {
			System.out.println("Aún no tiene viajes creados");
			System.out.println();
			return false;
		}
		res.beforeFirst();

		System.out.printf("%5s%20s%20s%20s%20s%40s%10s\n", rmd.getColumnName(1), rmd.getColumnName(2),
				rmd.getColumnName(3), rmd.getColumnName(4), rmd.getColumnName(5), rmd.getColumnName(6),
				rmd.getColumnName(7));
		for (int i = 0; i < 5 + 20 + 20 + 20 + 20 + 40 + 10; i++) {
			System.out.print("*");
		}
		System.out.println();
		while (res.next()) {
			System.out.printf("%5s%20s%20s%20s%20s%40s%10s\n", String.valueOf(res.getInt(1)),
					String.valueOf(res.getDate(2)), res.getString(3), String.valueOf(res.getDate(4)),
					String.valueOf(res.getDate(5)), res.getString(6), String.valueOf(res.getInt(7)));
		}
		System.out.println();
		return true;
	}

	public void crearViaje(String usuario, String nombre, String fechaInicio, String fechaFin, String imagen)
			throws SQLException {
		String consulta = "insert into viaje(fecha_creacion, nombre, fecha_inicio, fecha_fin, imagen, creador) "
				+ "values(?,?,?,?,?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setDate(1, Date.valueOf(LocalDate.now()));
		ps.setString(2, nombre);
		ps.setDate(3, Date.valueOf(fechaInicio));
		ps.setDate(4, Date.valueOf(fechaFin));
		ps.setString(5, imagen);
		ps.setInt(6, buscarIdUsuario(usuario));

		ps.executeUpdate();
		int idViaje = idUltimoViaje();
		asociarViajeUsuario(idViaje, usuario);
		crearItinerarioViaje(idViaje, fechaInicio, fechaFin);
		System.out.println("Viaje creado con éxito :)");

	}

	public int idUltimoViaje() throws SQLException {
		String consulta = "select id from viaje;";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.last() ? res.getInt(1) : null;
	}

	public void asociarViajeUsuario(int idViaje, String usuario) throws SQLException {
		int idUsuario = buscarIdUsuario(usuario);
		String consulta = "insert into usuario_viaje values(?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setInt(1, idUsuario);
		ps.setInt(2, idViaje);
		ps.executeUpdate();
	}

	public void crearItinerarioViaje(int idViaje, String fechainicio, String fechaFin) throws SQLException {
		// Primero calculamos los dias
		LocalDate ldateInicio = LocalDate.parse(fechainicio);
		LocalDate ldateFin = LocalDate.parse(fechaFin);
		int dias = (int) ldateInicio.until(ldateFin, ChronoUnit.DAYS);

		String consulta = "insert into itinerario(fecha, id_viaje) values(?,?);";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		for (int i = 0; i <= dias; i++) {
			ps.setDate(1, Date.valueOf(ldateInicio.plusDays(i)));
			ps.setInt(2, idViaje);
			ps.executeUpdate();
		}
	}

	public boolean buscarViajeUsuario(String usuario, int idViaje) throws SQLException {
		int idUsuario = buscarIdUsuario(usuario);

		String consulta = "select * from viaje join usuario_viaje on viaje.id=usuario_viaje.id_viaje "
				+ "where id_usuario = " + idUsuario + " " + "and id_viaje = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.first();
	}

	public void eliminarViaje(int idViaje) throws SQLException {
		String consulta = "";
		PreparedStatement ps = null;
		ResultSet res = null;

		// Eliminar Alojamiento
		consulta = "delete from alojamiento where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		ps.executeUpdate();

		// Eliminar usuario_viaje
		consulta = "delete from usuario_viaje where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		ps.executeUpdate();

		// Eliminar itinerarios
		consulta = "select id from itinerario where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		res = ps.executeQuery();
		String consulta1 = "";
		PreparedStatement ps1 = null;
		while (res.next()) {
			consulta1 = "delete from actividad where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
			consulta1 = "delete from foto where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
			consulta1 = "delete from billete where id_itinerario = " + res.getInt(1) + ";";
			ps1 = conexion.prepareStatement(consulta1);
			ps1.executeUpdate();
		}

		consulta = "delete from itinerario where id_viaje = " + idViaje + ";";
		ps = conexion.prepareStatement(consulta);
		ps.executeUpdate();

	}

	public void mostrarItinerario(int idViaje) throws SQLException {
		String consulta = "select itinerario.fecha from itinerario join viaje on itinerario.id_viaje=viaje.id "
				+ "where id_viaje = " + idViaje + ";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();
		ResultSetMetaData rmd = res.getMetaData();

		System.out.printf("%20s\n", rmd.getColumnName(1));
		for (int i = 0; i < 20; i++) {
			System.out.print("*");
		}
		System.out.println();
		while (res.next()) {
			System.out.printf("%20s\n", String.valueOf(res.getDate(1)));
		}
		System.out.println();
	}

	public int idItinerario(int idViaje, String fecha) throws SQLException {
		String consulta = "select id from itinerario where id_viaje = " + idViaje + " and fecha = \""
				+ fecha + "\";";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ResultSet res = ps.executeQuery();

		return res.next() ? res.getInt(1) : 0;
	}

	public void crearActividad(String direccion, String ciudad, String pais, String hora, String duracion,
			int idItinerario) throws SQLException {
		String consulta = "insert into actividad(direccion, ciudad, pais, hora_inicio, duracion, id_itinerario) "
				+ "values(?,?,?,?,?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, direccion);
		ps.setString(2, ciudad);
		ps.setString(3, pais);
		ps.setTime(4, Time.valueOf(hora));
		ps.setTime(5, Time.valueOf(duracion));
		ps.setInt(6, idItinerario);
		ps.executeUpdate();
	}

	public void subirFoto(String nombre, String ruta, int idItinerario) throws SQLException {
		String consulta = "insert into foto(nombre, ruta_archivo, id_itinerario) values(?,?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, nombre);
		ps.setString(2, ruta);
		ps.setInt(3, idItinerario);
		ps.executeUpdate();
	}

	public void subirBillete(String nombre, String transporte, String origen, String destino, String horaSalida,
			String horaLlegada, String compania, String identificador, String ruta, int idItinerario)
			throws SQLException {
		String consulta = "insert into billete(nombre, transporte, origen, destino, hora_salida, hora_llegada, "
				+ "compannia, identificador, ruta_archivo, id_itinerario) values(?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = conexion.prepareStatement(consulta);
		ps.setString(1, nombre);
		ps.setString(2, transporte);
		ps.setString(3, origen);
		ps.setString(4, destino);
		ps.setTime(5, Time.valueOf(horaSalida));
		ps.setTime(6, Time.valueOf(horaLlegada));
		ps.setString(7, compania);
		ps.setString(8, identificador);
		ps.setString(9, ruta);
		ps.setInt(10, idItinerario);
		ps.executeUpdate();
	}

}
