package np.playground.core.osgi.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private ApplicationListener applicationListener;
    private ViewListener viewListener;
    private InterceptorListener interceptorListener;

    @Override
    public void start(BundleContext context) throws Exception {
        applicationListener = new ApplicationListener(context);
        viewListener = new ViewListener(context);
        interceptorListener = new InterceptorListener(context);
        context.addServiceListener(applicationListener);
        System.out.println("Application listener registered");
        context.addServiceListener(viewListener);
        System.out.println("View listener registered");
        context.addServiceListener(interceptorListener);
        System.out.println("Interceptor listener registered");

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        System.out.println("Closing Playground !!!");
    }
}
