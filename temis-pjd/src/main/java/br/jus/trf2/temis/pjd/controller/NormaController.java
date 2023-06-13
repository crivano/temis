package br.jus.trf2.temis.pjd.controller;

import java.util.List;

import com.crivano.juia.util.JuiaUtils;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.jus.trf2.temis.core.controller.ControllerSupport;
import br.jus.trf2.temis.pjd.model.Norma;

@Controller
@Path("app/pro-norma")
public class NormaController extends ControllerSupport<Norma> {

	@Override
	protected Div templateExibir(Class<Norma> clazz) throws Exception {
		Div div = super.templateExibir(clazz);
		Div p = (Div) JuiaUtils.findChildByAttribute(div, new CustomAttribute("id", "content"));
		Div col = (Div) p.getParent();

		List<AbstractHtml> children = col.getChildren();
		col.removeAllChildren();
		new CustomTag("norma-show", col, new CustomAttribute(":data", "data"));
		col.appendChildren(children);
		return div;
	}

}
