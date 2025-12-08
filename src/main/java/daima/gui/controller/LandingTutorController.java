package daima.gui.controller;

public class LandingTutorController extends LandingController {
  public void onClickReviewTutoringSessionReportList() {
  }

  public void onClickReviewTutoringSessionList() {
    ReviewTutoringSessionListController.navigateToTutoringSessionListPage(getScene());
  }

  public void onClickReviewTutoredList() {
    ReviewTutoredListTutorController.navigateToTutoredListPage(getScene());
  }

  public void onClickReviewIssueList() {

  }
}