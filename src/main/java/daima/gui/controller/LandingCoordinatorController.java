package daima.gui.controller;

public class LandingCoordinatorController extends LandingController {
  public void onClickReviewTutoringSessionReportList() {

  }

  public void onClickReviewTutoredList() {
    ReviewTutoredListCoordinatorController.navigateToTutoredListPage(getScene());
  }

  public void onClickReviewTutorList() {
  }

  public void onClickReviewGeneralReportList() {
    ReviewGeneralReportListController.navigateToGeneralReportListPage(getScene());
  }

  public void onClickReviewTutoringSessionPlanList() {
    ReviewTutoringSessionPlanListController.navigateToTutoringSessionPlanListPage(getScene());
  }
}