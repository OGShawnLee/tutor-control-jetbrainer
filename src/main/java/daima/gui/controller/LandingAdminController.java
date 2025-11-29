package daima.gui.controller;

public class LandingAdminController extends LandingController {
  public void onClickReviewStaffList() {
    ReviewStaffListController.navigateToStaffListPage(getScene());
  }

  public void onClickRegisterStaff() {
    RegisterStaffController.displayRegisterStaffModal(null);
  }

  public void onClickReviewTutoredList() {
    ReviewTutoredListAdminController.navigateToTutoredListPage(getScene());
  }

  public void onClickRegisterTutored() {
    RegisterTutoredController.displayRegisterTutoredModal(null);
  }

  public void onClickReviewProgramList() {
    ReviewProgramListController.navigateToProgramListPage(getScene());
  }

  public void onClickRegisterProgram() {
    RegisterProgramController.displayRegisterProgramModal(null);
  }
}