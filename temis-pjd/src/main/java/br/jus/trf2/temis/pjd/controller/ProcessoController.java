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
import br.jus.trf2.temis.pjd.model.Processo;

@Controller
@Path("app/pro-processo")
public class ProcessoController extends ControllerSupport<Processo> {

	@Override
	protected Div templateExibir(Class<Processo> clazz) throws Exception {
		Div div = super.templateExibir(clazz);
		Div p = (Div) JuiaUtils.findChildByAttribute(div, new CustomAttribute("id", "content"));
		Div col = (Div) p.getParent();

		List<AbstractHtml> children = col.getChildren();
		col.removeAllChildren();
		new CustomTag("processo-show", col, new CustomAttribute("ref", "processoShow"),
				new CustomAttribute(":data", "data"), new CustomAttribute(":action", "action"),
				new CustomAttribute(":event", "event"));
		col.appendChildren(children);
		return div;
	}

}
