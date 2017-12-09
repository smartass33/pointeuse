/**
 * JQuery Shopping List ( http://tuts.guillaumevoisin.fr/jquery/shoppingList/ ) 
 * Copyright (c) Guillaume Voisin 2010
 */
 
(function($){
	$.fn.shoppingList = function(options) {

		// Options par defaut
		var defaults = {
		};
				
		var options = $.extend(defaults, options);
		
		this.each(function(){
				
			var obj = $(this);
			
			// Empêcher la sélection des éléments à la souris (meilleure gestion du drag & drop)
			var _preventDefault = function(evt) { evt.preventDefault(); };
			$("li").bind("dragstart", _preventDefault).bind("selectstart", _preventDefault);

			// Initialisation du composant "sortable"
			$(obj).sortable({
				axis: "y", // Le sortable ne s'applique que sur l'axe vertical
				containment: ".shoppingList", // Le drag ne peut sortir de l'élément qui contient la liste
				handle: ".item", // Le drag ne peut se faire que sur l'élément .item (le texte)
				distance: 10, // Le drag ne commence qu'à partir de 10px de distance de l'élément
				// Evenement appelé lorsque l'élément est relaché
				stop: function(event, ui){
					// Pour chaque item de liste
					$(obj).find("li").each(function(){
						// On actualise sa position
						index = parseInt($(this).index()+1);
						// On la met à jour dans la page
						$(this).find(".count").text(index);
					});
				},
				update: function()
				{
					// On prépare la variable contenant les paramètres
					var order = $(this).sortable("serialize")+'&action=updateListeOrder';
					// $(this).sortable("serialize") sera le paramètre "element", un tableau contenant les différents "id"
					// action sera le paramètre qui permet éventuellement par la suite de gérer d'autres scripts de mise à jour
			 
					// Ensuite on appelle notre page updateListe.php en lui passant en paramètre la variable order
					$.post("/inc/updateListe.php", order, function(theResponse)
					{
						// On affiche dans l'élément portant la classe "reponse" le résultat du script de mise à jour
						$(".reponse").html(theResponse).fadeIn("fast");
						setTimeout(function()
						{
							$(".reponse").fadeOut("slow");
						}, 2000);
					});
				}
			});

			// Pour chaque élément trouvé dans la liste de départ
			$(obj).find("li").each(function(){
				// On ajoute les contrôles
				addControls($(this));
			});
			
			/*
			* Fonction qui ajoute les contrôles aux items
			* @Paramètres
			*  - elt: élément courant (liste courante)
			*
			* @Return void
			*/
			
			function addControls(elt)
			{
				// On ajoute en premier l'élément textuel
				$(elt).html("<span class='item'>"+$(elt).text()+"</item>");
				// Puis l'élément de position
				$(elt).prepend('<span class="count">'+parseInt($(elt).index()+1)+'</span>');

			}
		});
		// On continue le chainage JQuery
		return this;
	};
})(jQuery);