package sls.transferenciaeletronica.api.configuracao;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import sls.transferenciaeletronica.core.configuracao.ConfiguracaoDataSource;
import sls.transferenciaeletronica.core.configuracao.PropriedadesCore;
import sls.transferenciaeletronica.manager.configuracao.ConfiguracaoManagerDataSource;

/**
 * Classe que inicializa toda a aplicacao. Devo registrar as classes de
 * Configuracao e defino tambem o profile ativo do Spring, com base na variavel
 * ambiente que foi passado para o arquivo core.properties do modulo-core.
 *
 */
public class InicializadorAplicacao implements WebApplicationInitializer {

    final Logger logger = LoggerFactory.getLogger(InicializadorAplicacao.class);

    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ConfiguracaoAplicacao.class);
        rootContext.register(ConfiguracaoDataSource.class);
        rootContext.register(ConfiguracaoManagerDataSource.class);

        servletContext.addListener(new ContextLoaderListener(rootContext));
        servletContext.addListener(RequestContextListener.class);
        
        servletContext.setInitParameter(AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME, "desenvolvimento");
        String profileAtivo = PropriedadesCore.obterPropriedade("configuracao.ambiente");
        logger.info("Definindo profile ativo: {}", profileAtivo);
        servletContext.setInitParameter(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, profileAtivo);

        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();

        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/api/*");
    }
}
