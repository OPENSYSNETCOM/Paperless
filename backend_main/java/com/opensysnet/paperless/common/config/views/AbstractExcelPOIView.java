package com.opensysnet.paperless.common.config.views;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class AbstractExcelPOIView extends AbstractView {

	private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	public AbstractExcelPOIView() {
	}

	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	@Override
	protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) {
		try {
			SXSSFWorkbook workbook = new SXSSFWorkbook(10000);

			setContentType(CONTENT_TYPE_XLSX);

			if (model.get("requestType").equals("INSTALL")) {
				buildExcelDocument_Install(request, response, workbook, model);
			}
			else if (model.get("requestType").equals("SERVICE")) {
				buildExcelDocument_Service(request, response, workbook, model);
			} else {
				return;
			}


			response.setContentType(getContentType());

			ServletOutputStream out = response.getOutputStream();
			out.flush();

			workbook.write(out);
			out.flush();

			if(workbook instanceof SXSSFWorkbook) {
				workbook.dispose();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract Workbook createWorkbook();

	protected abstract void buildExcelDocument_Install(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook workbook, Map<String, Object> model);

	protected abstract void buildExcelDocument_Service(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook workbook, Map<String, Object> model);
}
