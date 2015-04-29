package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.ExportSpreadsheetService;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentIntegrator extends BubbleDocsIntegrator {

    private int docId;
    private String userToken;
    private byte[] xml;

    public ExportDocumentIntegrator(String token, int doc) {
        docId = doc;
        userToken = token;
    }

    public byte[] getDocXML() {
        return xml;
    }

    @Override
    public void execute() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(docId, userToken);
        service.execute();
        xml = service.getResult();

        Spreadsheet ss = BubbleDocs.getInstance().getSpreadsheetById(docId);
        User user = getBubbleDocs().getSessionManager().findUserByToken(userToken);
        StoreRemoteServices remote = new StoreRemoteServices();

        try {
            remote.storeDocument(user.getName(), ss.getName(), getDocXML());
        } catch (RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }

    }
}