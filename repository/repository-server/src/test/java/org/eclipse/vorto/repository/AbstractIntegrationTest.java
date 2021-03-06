package org.eclipse.vorto.repository;

import static org.mockito.Mockito.when;

import org.apache.commons.io.IOUtils;
import org.eclipse.vorto.repository.account.Role;
import org.eclipse.vorto.repository.account.impl.IUserRepository;
import org.eclipse.vorto.repository.account.impl.User;
import org.eclipse.vorto.repository.api.ModelInfo;
import org.eclipse.vorto.repository.core.IUserContext;
import org.eclipse.vorto.repository.core.impl.InMemoryTemporaryStorage;
import org.eclipse.vorto.repository.core.impl.JcrModelRepository;
import org.eclipse.vorto.repository.core.impl.UserContext;
import org.eclipse.vorto.repository.core.impl.utils.ModelSearchUtil;
import org.eclipse.vorto.repository.core.impl.validation.AttachmentValidator;
import org.eclipse.vorto.repository.importer.FileUpload;
import org.eclipse.vorto.repository.importer.UploadModelResult;
import org.eclipse.vorto.repository.importer.impl.VortoModelImporter;
import org.eclipse.vorto.repository.workflow.IWorkflowService;
import org.eclipse.vorto.repository.workflow.WorkflowException;
import org.eclipse.vorto.repository.workflow.impl.DefaultWorkflowService;
import org.eclipse.vorto.repository.workflow.impl.SimpleWorkflowModel;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modeshape.test.ModeShapeSingleUseTest;
import org.springframework.core.io.ClassPathResource;

public abstract class AbstractIntegrationTest extends ModeShapeSingleUseTest {

	@InjectMocks
	protected JcrModelRepository modelRepository;
	
	@InjectMocks
	protected ModelSearchUtil modelSearchUtil = new ModelSearchUtil();
	
	@Mock
	protected IUserRepository userRepository = Mockito.mock(IUserRepository.class);
	
	@Mock
	protected AttachmentValidator attachmentValidator = Mockito.mock(AttachmentValidator.class);
	
	protected VortoModelImporter importer = null;
	
	protected IWorkflowService workflow = null;
	
	public void beforeEach() throws Exception {
		super.beforeEach();
		startRepositoryWithConfiguration(new ClassPathResource("vorto-repository.json").getInputStream());

		Mockito.when(userRepository.findByUsername("alex")).thenReturn(User.create("alex"));
		Mockito.when(userRepository.findByUsername("admin")).thenReturn(User.create("admin", Role.ADMIN));
		
		modelRepository = new JcrModelRepository();
		modelRepository.setSession(jcrSession());
		modelRepository.setUserRepository(userRepository);
		modelRepository.setModelSearchUtil(modelSearchUtil);
		
		this.importer = new VortoModelImporter();
		this.importer.setModelRepository(modelRepository);
		this.importer.setUploadStorage(new InMemoryTemporaryStorage());
		this.importer.setUserRepository(userRepository);
		
		this.workflow = new DefaultWorkflowService(this.modelRepository,userRepository);


	}

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}
	
	protected ModelInfo importModel(String modelName) {
		return importModel(modelName, UserContext.user(getCallerId()));
	}
	
	protected String getCallerId() {
		return "alex";
	}
	
	protected ModelInfo importModel(String modelName, IUserContext userContext) {
		try {
			UploadModelResult uploadResult = this.importer.upload(FileUpload.create(modelName,
					IOUtils.toByteArray(new ClassPathResource("sample_models/" + modelName).getInputStream())), userContext);			
			return this.importer.doImport(uploadResult.getHandleId(), userContext).get(0);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected ModelInfo setReleaseState(ModelInfo model) throws WorkflowException {
		when(userRepository.findByUsername(UserContext.user(getCallerId()).getUsername())).thenReturn(User.create(getCallerId(),Role.USER));
		workflow.doAction(model.getId(),UserContext.user(getCallerId()), SimpleWorkflowModel.ACTION_RELEASE.getName());	
		when(userRepository.findByUsername(UserContext.user("admin").getUsername())).thenReturn(User.create("admin",Role.ADMIN));
		return workflow.doAction(model.getId(),UserContext.user("admin"), SimpleWorkflowModel.ACTION_APPROVE.getName());
	}
}
