app.directive('popOver', function ($compile) {
    return {
        restrict: "A",
        transclude: true,
        template: "<span ng-transclude></span>",
        link: function (scope, element, attrs) {
            var popOverContent;
            var html = "<div ng-include=\"template\"></div>";
            popOverContent = $compile(html)(scope);

            var options = {
                content: popOverContent,
                placement: "left",
                html: true,
                title: scope.title
            };
            $(element).popover(options);
        },
        scope: {
            items: '=',
            title: '@',
            template: "@popOver"
        }
    };
});
