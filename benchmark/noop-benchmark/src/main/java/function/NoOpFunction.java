package function;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

public class NoOpFunction implements HttpFunction, RequestHandler<NoOpInput, NoOpOutput> {

    private static final Gson gson = new Gson();

    @Override
    public NoOpOutput handleRequest(NoOpInput input, Context context) {
        return new NoOpOutput();
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        response.getWriter().write(gson.toJson(new NoOpInput()));
    }

}
