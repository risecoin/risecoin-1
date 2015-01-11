package rise.http;

import rise.Poll;
import rise.util.Convert;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

import static rise.http.JSONResponses.INCORRECT_POLL;
import static rise.http.JSONResponses.MISSING_POLL;
import static rise.http.JSONResponses.UNKNOWN_POLL;

public final class GetPoll extends APIServlet.APIRequestHandler {

    static final GetPoll instance = new GetPoll();

    private GetPoll() {
        super(new APITag[] {APITag.VS}, "poll");
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) {

        String poll = req.getParameter("poll");
        if (poll == null) {
            return MISSING_POLL;
        }

        Poll pollData;
        try {
            pollData = Poll.getPoll(Convert.parseUnsignedLong(poll));
            if (pollData == null) {
                return UNKNOWN_POLL;
            }
        } catch (RuntimeException e) {
            return INCORRECT_POLL;
        }

        return JSONData.poll(pollData);

    }

}
