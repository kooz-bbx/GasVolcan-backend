package cl.gasvolcan.api.config;

import cl.gasvolcan.api.model.*;
import cl.gasvolcan.api.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Carga los datos iniciales la primera vez que se levanta el backend (si las tablas están vacías). */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner cargarDatosIniciales(CategoriaRepository categorias, ProductoRepository productos,
                                                  CamionRepository camiones, UsuarioRepository usuarios,
                                                  PasswordEncoder encoder) {
        return args -> {
            if (categorias.count() == 0) {
                categorias.save(new Categoria("residencial", "Uso Residencial",
                        "Cilindros para cocina y calefacción del hogar."));
                categorias.save(new Categoria("comercial", "Uso Comercial e Industrial",
                        "Cilindros de gran capacidad para restaurantes, talleres y locales."));
            }
            if (productos.count() == 0) {
                productos.save(producto("CL001", "residencial", "Cilindro GLP 5 kg",
                        "Cilindro de gas licuado de petróleo 5 kg. Para uso residencial (cocina, calefacción pequeña).",
                        6500, 10, 80, 15, "img/gas5.jpg"));
                productos.save(producto("CL002", "residencial", "Cilindro GLP 11 kg",
                        "Cilindro estándar doméstico. El más utilizado en hogares chilenos. Compatible con reguladores estándar.",
                        12000, 0, 200, 30, "img/gas11.jpg"));
                productos.save(producto("CL003", "residencial", "Cilindro GLP 15 kg",
                        "Cilindro de mayor capacidad para hogares de alto consumo o locales pequeños.",
                        16000, 15, 90, 20, "img/gas15.jpg"));
                productos.save(producto("CL004", "comercial", "Cilindro GLP 45 kg",
                        "Cilindro industrial. Uso comercial: restaurantes, talleres, calefacción de locales.",
                        45000, 0, 30, 5, "img/gas45.jpg"));
            }
            if (camiones.count() == 0) {
                camiones.save(new Camion("KLRT-11", "Camión 3/4 Hyundai HD65", 60));
                camiones.save(new Camion("PHZX-45", "Camión Mitsubishi Canter", 80));
            }
            if (usuarios.count() == 0) {
                usuarios.save(usuario(encoder, "111111111", "Benjamín", "Alegria Rojas", "admin@duoc.cl", "admin123",
                        "Región de Ñuble", "Chillán", "Oficina Central 100, Chillán", TipoUsuario.ADMINISTRADOR));
                usuarios.save(usuario(encoder, "333333333", "Camila", "Soto Reyes", "operadora@duoc.cl", "opera123",
                        "Región de Ñuble", "Chillán", "Central de despacho 300, Chillán", TipoUsuario.OPERADORA));
                usuarios.save(usuario(encoder, "444444444", "Enzo", "Ramírez Soto", "repenzo@duoc.cl", "reparte123",
                        "Región de Ñuble", "Chillán", "Villa Los Aromos 12, Chillán", TipoUsuario.REPARTIDOR));
                usuarios.save(usuario(encoder, "555555555", "Javiera", "Muñoz Vera", "repjaviera@duoc.cl", "reparte123",
                        "Región de Ñuble", "Chillán Viejo", "Calle Las Rosas 45, Chillán Viejo", TipoUsuario.REPARTIDOR));
                usuarios.save(usuario(encoder, "666666666", "Nicolás", "Ortiz Díaz", "repnico@duoc.cl", "reparte123",
                        "Región de Ñuble", "San Carlos", "Pasaje Los Olivos 8, San Carlos", TipoUsuario.REPARTIDOR));
            }
        };
    }

    private Producto producto(String codigo, String categoriaId, String nombre, String descripcion,
                              int precio, int descuento, int stock, int stockCritico, String imagen) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setCategoriaId(categoriaId);
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setPrecio(precio);
        p.setDescuento(descuento);
        p.setStock(stock);
        p.setStockCritico(stockCritico);
        p.setImagen(imagen);
        return p;
    }

    private Usuario usuario(PasswordEncoder encoder, String run, String nombre, String apellidos, String correo,
                            String clave, String region, String comuna, String direccion, TipoUsuario tipo) {
        Usuario u = new Usuario();
        u.setRun(run);
        u.setNombre(nombre);
        u.setApellidos(apellidos);
        u.setCorreo(correo);
        u.setContrasena(encoder.encode(clave));
        u.setRegion(region);
        u.setComuna(comuna);
        u.setDireccion(direccion);
        u.setTipoUsuario(tipo);
        return u;
    }
}
