package pointeuse

class UITagLib {
    static namespace = "myui"
 
    def modalDialog = { attrs, body ->
        def a = [
            modal: true,
            autoOpen: false,
            height: 400,
            width: 400,
            name: "jquiDialog${System.currentTimeMillis()}",
            params: '',
            selector: ''
        ] + attrs
 
        def name = a.remove('name')
        def action = a.remove('action')
        def controller = a.remove('controller')
        def selector = a.remove('selector')
        def params = a.remove('params')
        def url = a.remove('url')
        def onLoaded = a.remove('onLoaded')
        def openerId = a.remove('openerId')
        if (!openerId) {
            openerId = "btn_${name}"
        }
 
        if (selector) {
            selector = " ${selector}"
        }
        if (!url) {
            url = g.createLink(controller: controller, action: action)
        }
        def bodyContents=body()
        out << """
<script type=\"text/javascript\">
    \$(function() {
        \$("#${name}").dialog({"""
        a.each {k, v ->
            out << """
            ${k}:${v},
"""
        }
        out << """
            dummy:'dummy'
        })
        \$(\"#${openerId}\").click(function() {"""
        //if body is non-empty, then the dialog will not be loaded dynamically
        if (!bodyContents) {
            out << """
                \$("#${name}").load('${url}${selector}', {${params}}, function() {"""
            if (onLoaded) {
                out << """
                    ${onLoaded}()"""
            }
            out << """
                    \$("#${name}").dialog('open')
            })"""
        } else {
            out << """
                    \$("#${name}").dialog('open')"""
        }
        out << """
        })
    })
</script>
<div id="${name}">${bodyContents}</div>
"""
    }
}