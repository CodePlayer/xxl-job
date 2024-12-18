$(function() {

	// init date tables
	var $jobList = $("#job_list");
	var jobTable = $jobList.dataTable({
		"deferRender": true,
		"processing" : true,
	    "serverSide": true,
		"ajax": {
			url: base_url + "/jobinfo/pageList",
			type:"post",
	        data : function ( d ) {
	        	var obj = {};
	        	obj.jobGroup = $('#jobGroup').val();
                obj.triggerStatus = $('#triggerStatus').val();
                obj.jobDesc = $('#jobDesc').val();
	        	obj.executorHandler = $('#executorHandler').val();
                obj.author = $('#author').val();
	        	obj.start = d.start;
	        	obj.length = d.length;
                return obj;
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,	// scroll x，close self-adaption
	    "columns": [
	                {
	                	"data": 'id',
						"bSortable": false,
						"visible" : true,
						"width":'7%'
					},
	                {
	                	"data": 'jobGroup',
	                	"visible" : false,
	                	"render": function ( data, type, row ) {
	            			var groupMenu = $("#jobGroup").find("option");
					for (var index in groupMenu) {
	            				if ($(groupMenu[index]).attr('value') == data) {
									return $(groupMenu[index]).html();
								}
							}
	            			return data;
	            		}
            		},
	                {
	                	"data": 'jobDesc',
						"visible" : true,
						"width":'25%'
					},
					{
						"data": 'scheduleType',
						"visible" : true,
						"width":'13%',
						"render": function ( data, type, row ) {
							if (row.scheduleConf) {
								return row.scheduleType + '：'+ row.scheduleConf;
							} else {
								return row.scheduleType;
							}
						}
					},
					{
						"data": 'glueType',
						"width":'25%',
						"visible" : true,
						"render": function ( data, type, row ) {
							var glueTypeTitle = findGlueTypeTitle(row.glueType);
                            if (row.executorHandler) {
                                return glueTypeTitle +"：" + row.executorHandler;
                            } else {
                                return glueTypeTitle;
                            }
						}
					},
	                { "data": 'executorParam', "visible" : false},
	                {
	                	"data": 'addTime',
	                	"visible" : false,
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                {
	                	"data": 'updateTime',
	                	"visible" : false,
	                	"render": function ( data, type, row ) {
	                		return data?moment(new Date(data)).format("YYYY-MM-DD HH:mm:ss"):"";
	                	}
	                },
	                { "data": 'author', "visible" : true, "width":'10%'},
	                { "data": 'alarmEmail', "visible" : false},
	                {
	                	"data": 'triggerStatus',
						"width":'10%',
	                	"visible" : true,
	                	"render": function ( data, type, row ) {
                            // status
                            if (1 == data) {
                                return '<small class="label label-success" >RUNNING</small>';
                            } else {
                                return '<small class="label label-default" >STOP</small>';
                            }
	                	}
	                },
	                {
						"data": I18n.system_opt ,
						"width":'10%',
	                	"render": function ( data, type, row ) {
	                		return function(){

                                // status
						var start_stop_div;
                                if (1 == row.triggerStatus ) {
                                    start_stop_div = '<li><a href="javascript:void(0);" class="job_operate" _type="job_pause" >'+ I18n.jobinfo_opt_stop +'</a></li>\n';
                                } else {
                                    start_stop_div = '<li><a href="javascript:void(0);" class="job_operate" _type="job_resume" >'+ I18n.jobinfo_opt_start +'</a></li>\n';
                                }

                                // job_next_time_html
								var job_next_time_html = '';
								if (row.scheduleType == 'CRON' || row.scheduleType == 'FIX_RATE') {
									job_next_time_html = '<li><a href="javascript:void(0);" class="job_next_time" >' + I18n.jobinfo_opt_next_time + '</a></li>\n';
								}

                                // log url
                                var logHref = base_url +'/joblog?jobId='+ row.id;

                                // code url
                                var codeBtn = "";
                                if ('BEAN' != row.glueType) {
                                    var codeUrl = base_url +'/jobcode?jobId='+ row.id;
                                    codeBtn = '<li><a href="'+ codeUrl +'" target="_blank" >GLUE IDE</a></li>\n';
                                    codeBtn += '<li class="divider"></li>\n';
                                }

                                // data
                                tableData['key'+row.id] = row;

                                // opt
                                var html = '<div class="btn-group">\n' +
                                    '     <button type="button" class="btn btn-primary btn-sm">'+ I18n.system_opt +'</button>\n' +
                                    '     <button type="button" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">\n' +
                                    '       <span class="caret"></span>\n' +
                                    '       <span class="sr-only">Toggle Dropdown</span>\n' +
                                    '     </button>\n' +
                                    '     <ul class="dropdown-menu" role="menu" _id="'+ row.id +'" >\n' +
                                    '       <li><a href="javascript:void(0);" class="job_trigger" >'+ I18n.jobinfo_opt_run +'</a></li>\n' +
                                    '       <li><a href="'+ logHref +'">'+ I18n.jobinfo_opt_log +'</a></li>\n' +
                                    '       <li><a href="javascript:void(0);" class="job_registryinfo" >' + I18n.jobinfo_opt_registryinfo + '</a></li>\n' +
									job_next_time_html +
                                    '       <li class="divider"></li>\n' +
                                    codeBtn +
                                    start_stop_div +
                                    '       <li><a href="javascript:void(0);" class="update" >'+ I18n.system_opt_edit +'</a></li>\n' +
                                    '       <li><a href="javascript:void(0);" class="job_operate" _type="job_del" >'+ I18n.system_opt_del +'</a></li>\n' +
									'       <li><a href="javascript:void(0);" class="job_copy" >'+ I18n.system_opt_copy +'</a></li>\n' +
                                    '     </ul>\n' +
                                    '   </div>';

	                			return html;
							};
	                	}
	                }
	            ],
		"language" : {
			"sProcessing" : I18n.dataTable_sProcessing ,
			"sLengthMenu" : I18n.dataTable_sLengthMenu ,
			"sZeroRecords" : I18n.dataTable_sZeroRecords ,
			"sInfo" : I18n.dataTable_sInfo ,
			"sInfoEmpty" : I18n.dataTable_sInfoEmpty ,
			"sInfoFiltered" : I18n.dataTable_sInfoFiltered ,
			"sInfoPostFix" : "",
			"sSearch" : I18n.dataTable_sSearch ,
			"sUrl" : "",
			"sEmptyTable" : I18n.dataTable_sEmptyTable ,
			"sLoadingRecords" : I18n.dataTable_sLoadingRecords ,
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : I18n.dataTable_sFirst ,
				"sPrevious" : I18n.dataTable_sPrevious ,
				"sNext" : I18n.dataTable_sNext ,
				"sLast" : I18n.dataTable_sLast
			},
			"oAria" : {
				"sSortAscending" : I18n.dataTable_sSortAscending ,
				"sSortDescending" : I18n.dataTable_sSortDescending
			}
		}
	});

    // table data
    var tableData = {};

	// search btn
	$('#searchBtn').on('click', function(){
		jobTable.fnDraw();
	});

	// jobGroup change
	$('#jobGroup').on('change', function(){
        //reload
        var jobGroup = $('#jobGroup').val();
        window.location.href = base_url + "/jobinfo?jobGroup=" + jobGroup;
    });

	// job operate
	$jobList.on('click', '.job_operate', function () {
		var typeName;
		var url;
		var needFresh = false;

		var type = $(this).attr("_type");
		if ("job_pause" == type) {
			typeName = I18n.jobinfo_opt_stop ;
			url = base_url + "/jobinfo/stop";
			needFresh = true;
		} else if ("job_resume" == type) {
			typeName = I18n.jobinfo_opt_start ;
			url = base_url + "/jobinfo/start";
			needFresh = true;
		} else if ("job_del" == type) {
			typeName = I18n.system_opt_del ;
			url = base_url + "/jobinfo/remove";
			needFresh = true;
		} else {
			return;
		}

		var id = $(this).parents('ul').attr("_id");

		layer.confirm( I18n.system_ok + typeName + '?', {
			icon: 3,
			title: I18n.system_tips ,
            btn: [ I18n.system_ok, I18n.system_cancel ]
		}, function(index){
			layer.close(index);

			$.ajax({
				type : 'POST',
				url : url,
				data : {
					"id" : id
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
                        layer.msg( typeName + I18n.system_success );
                        if (needFresh) {
                            jobTable.fnDraw(false);
                        }
					} else {
                        layer.msg( data.msg || typeName + I18n.system_fail );
					}
				}
			});
		});
	});

    // job trigger
	$jobList.on('click', '.job_trigger', function () {
        var id = $(this).parents('ul').attr("_id");
        var row = tableData['key'+id];

        $("#jobTriggerModal .form input[name='id']").val( row.id );
        $("#jobTriggerModal .form textarea[name='executorParam']").val( row.executorParam );

        $('#jobTriggerModal').modal({backdrop: false, keyboard: false}).modal('show');
    });
    $("#jobTriggerModal .ok").on('click',function() {
        $.ajax({
            type : 'POST',
            url : base_url + "/jobinfo/trigger",
            data : {
                "id" : $("#jobTriggerModal .form input[name='id']").val(),
                "executorParam" : $("#jobTriggerModal .textarea[name='executorParam']").val(),
				"addressList" : $("#jobTriggerModal .textarea[name='addressList']").val()
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    $('#jobTriggerModal').modal('hide');

                    layer.msg( I18n.jobinfo_opt_run + I18n.system_success );
                } else {
                    layer.msg( data.msg || I18n.jobinfo_opt_run + I18n.system_fail );
                }
            }
        });
    });
    $("#jobTriggerModal").on('hide.bs.modal', function () {
        $("#jobTriggerModal .form")[0].reset();
    });


    // job registryinfo
	$jobList.on('click', '.job_registryinfo', function () {
        var id = $(this).parents('ul').attr("_id");
        var row = tableData['key'+id];

        var jobGroup = row.jobGroup;

        $.ajax({
            type : 'POST',
            url : base_url + "/jobgroup/loadById",
            data : {
                "id" : jobGroup
            },
            dataType : "json",
            success : function(data){

                var html = '<div>';
                if (data.code == 200 && data.content.registryList) {
                    for (var index in data.content.registryList) {
                        html += (parseInt(index)+1) + '. <span class="badge bg-green" >' + data.content.registryList[index] + '</span><br>';
                    }
                }
                html += '</div>';

                layer.open({
                    title: I18n.jobinfo_opt_registryinfo ,
                    btn: [ I18n.system_ok ],
                    content: html
                });

            }
        });

    });

    // job_next_time
	$jobList.on('click', '.job_next_time', function () {
        var id = $(this).parents('ul').attr("_id");
        var row = tableData['key'+id];

        $.ajax({
            type : 'POST',
            url : base_url + "/jobinfo/nextTriggerTime",
            data : {
                "scheduleType" : row.scheduleType,
				"scheduleConf" : row.scheduleConf
            },
            dataType : "json",
            success : function(data){

            	if (data.code != 200) {
                    layer.open({
                        title: I18n.jobinfo_opt_next_time ,
                        btn: [ I18n.system_ok ],
                        content: data.msg
                    });
				} else {
                    var html = '<center>';
                    if (data.code == 200 && data.content) {
                        for (var index in data.content) {
                            html += '<span>' + data.content[index] + '</span><br>';
                        }
                    }
                    html += '</center>';

                    layer.open({
                        title: I18n.jobinfo_opt_next_time ,
                        btn: [ I18n.system_ok ],
                        content: html
                    });
				}

            }
        });

    });

	// add
	$(".add").click(function(){

		// init-cronGen
		var $schedule_conf_CRON = $("#addModal .form input[name='schedule_conf_CRON']");
		$schedule_conf_CRON.show().siblings().remove();
		$schedule_conf_CRON.cronGen({});

		// 》init scheduleType
		var $form = $("#updateModal .form");
		$form.find("select[name=scheduleType]").change();

		// 》init glueType
		$form.find("select[name=glueType]").change();

		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,
        rules : {
			jobDesc : {
				required : true,
				maxlength: 50
			},
			author : {
				required : true
			}/*,
            executorTimeout : {
                digits:true
            },
            executorFailRetryCount : {
                digits:true
            }*/
        },
        messages : {
            jobDesc : {
            	required : I18n.system_please_input + I18n.jobinfo_field_jobdesc
            },
            author : {
            	required : I18n.system_please_input + I18n.jobinfo_field_author
            }/*,
            executorTimeout : {
                digits: I18n.system_please_input + I18n.system_digits
            },
            executorFailRetryCount : {
                digits: I18n.system_please_input + I18n.system_digits
            }*/
        },
		highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success : function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement : function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler : function(form) {

			// process executorTimeout+executorFailRetryCount
			var $form = $("#addModal .form"), $executorTimeout = $form.find("input[name='executorTimeout']");
			var executorTimeout = $executorTimeout.val();
            if(!/^\d+$/.test(executorTimeout)) {
				$executorTimeout.val(executorTimeout = 0);
			}
			var $executorFailRetryCount = $form.find("input[name='executorFailRetryCount']");
			var executorFailRetryCount = $executorFailRetryCount.val();
            if(!/^\d+$/.test(executorFailRetryCount)) {
				$executorFailRetryCount.val(executorFailRetryCount = 0);
            }

            // process schedule_conf
			var scheduleType = $form.find("select[name='scheduleType']").val();
			var scheduleConf;
			if (scheduleType == 'CRON') {
				scheduleConf = $form.find("input[name='cronGen_display']").val();
			} else if (scheduleType == 'FIX_RATE') {
				scheduleConf = $form.find("input[name='schedule_conf_FIX_RATE']").val();
			} else if (scheduleType == 'FIX_DELAY') {
				scheduleConf = $form.find("input[name='schedule_conf_FIX_DELAY']").val();
			}
			$form.find("input[name='scheduleConf']").val(scheduleConf);

			$.post(base_url + "/jobinfo/add", $form.serialize(), function (data, status) {
    			if (data.code == "200") {
					$('#addModal').modal('hide');
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: I18n.system_add_suc ,
						icon: '1',
						end: function(layero, index){
							jobTable.fnDraw();
						}
					});
    			} else {
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: (data.msg || I18n.system_add_fail),
						icon: '2'
					});
    			}
    		});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
        addModalValidate.resetForm();
		var $form = $("#addModal .form");
		$form[0].reset();
		$form.find(".form-group").removeClass("has-error");
		$(".remote_panel").show();	// remote

		$form.find("input[name='executorHandler']").removeAttr("readonly");
	});

	// scheduleType change
	$(".scheduleType").change(function(){
		var $me = $(this), scheduleType = $me.val(), $form = $me.parents("form");
		$form.find(".schedule_conf").hide();
		$form.find(".schedule_conf_" + scheduleType).show();

	});

    // glueType change
    $(".glueType").change(function(){
		// executorHandler
		var $me = $(this), $executorHandler = $me.parents("form").find("input[name='executorHandler']");
		var glueType = $me.val();
        if ('BEAN' != glueType) {
            $executorHandler.val("");
            $executorHandler.attr("readonly","readonly");
        } else {
            $executorHandler.removeAttr("readonly");
        }
    });

	$("#addModal .glueType").change(function(){
		// glueSource
		var glueType = $(this).val(), $glueSource = $("#addModal .form textarea[name='glueSource']");
		if ('GLUE_GROOVY'==glueType){
			$glueSource.val($("#addModal .form .glueSource_java").val());
		} else if ('GLUE_SHELL'==glueType){
			$glueSource.val($("#addModal .form .glueSource_shell").val());
		} else if ('GLUE_PYTHON'==glueType){
			$glueSource.val($("#addModal .form .glueSource_python").val());
		} else if ('GLUE_PHP'==glueType){
			$glueSource.val($("#addModal .form .glueSource_php").val());
        } else if ('GLUE_NODEJS'==glueType){
			$glueSource.val($("#addModal .form .glueSource_nodejs").val());
		} else if ('GLUE_POWERSHELL'==glueType){
			$glueSource.val($("#addModal .form .glueSource_powershell").val());
        } else {
			$glueSource.val("");
		}
	});

	// update
	$jobList.on('click', '.update', function () {

        var id = $(this).parents('ul').attr("_id");
        var row = tableData['key'+id];

		// fill base
		var $form = $("#updateModal .form");
		$form.find("input[name='id']").val(row.id);
		$form.find('select[name=jobGroup]').val(row.jobGroup);
		$form.find("input[name='jobDesc']").val(row.jobDesc);
		$form.find("input[name='author']").val(row.author);
		$form.find("input[name='alarmEmail']").val(row.alarmEmail);

		// fill trigger
		$form.find('select[name=scheduleType]').val(row.scheduleType);
		$form.find("input[name='scheduleConf']").val(row.scheduleConf);
		if (row.scheduleType == 'CRON') {
			$form.find("input[name='schedule_conf_CRON']").val(row.scheduleConf);
		} else if (row.scheduleType == 'FIX_RATE') {
			$form.find("input[name='schedule_conf_FIX_RATE']").val(row.scheduleConf);
		} else if (row.scheduleType == 'FIX_DELAY') {
			$form.find("input[name='schedule_conf_FIX_DELAY']").val(row.scheduleConf);
		}

		// 》init scheduleType
		$form.find("select[name=scheduleType]").change();

		// fill job
		$form.find('select[name=glueType]').val(row.glueType);
		$form.find("input[name='executorHandler']").val(row.executorHandler);
		$form.find("textarea[name='executorParam']").val(row.executorParam);

		// 》init glueType
		$form.find("select[name=glueType]").change();

		// 》init-cronGen
		var $schedule_conf_CRON = $form.find("input[name='schedule_conf_CRON']");
		$schedule_conf_CRON.show().siblings().remove();
		$schedule_conf_CRON.cronGen({});

		// fill advanced
		$form.find('select[name=executorRouteStrategy]').val(row.executorRouteStrategy);
		$form.find("input[name='childJobId']").val(row.childJobId);
		$form.find("select[name=misfireStrategy]").val(row.misfireStrategy);
		$form.find('select[name=executorBlockStrategy]').val(row.executorBlockStrategy);
		$form.find("input[name='executorTimeout']").val(row.executorTimeout);
		$form.find("input[name='executorFailRetryCount']").val(row.executorFailRetryCount);

		// show
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',
        errorClass : 'help-block',
        focusInvalid : true,

		rules : {
			jobDesc : {
				required : true,
				maxlength: 50
			},
			author : {
				required : true
			}
		},
		messages : {
			jobDesc : {
                required : I18n.system_please_input + I18n.jobinfo_field_jobdesc
			},
			author : {
				required : I18n.system_please_input + I18n.jobinfo_field_author
			}
		},
		highlight : function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success : function(label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement : function(error, element) {
            element.parent('div').append(error);
        },
        submitHandler : function(form) {

            // process executorTimeout + executorFailRetryCount
			var $form = $("#updateModal .form");
			var $executorTimeout = $form.find("input[name='executorTimeout']");
			var executorTimeout = $executorTimeout.val();
            if(!/^\d+$/.test(executorTimeout)) {
				$executorTimeout.val(executorTimeout = 0);
            }
			var $executorFailRetryCount = $form.find("input[name='executorFailRetryCount']");
			var executorFailRetryCount = $executorFailRetryCount.val();
            if(!/^\d+$/.test(executorFailRetryCount)) {
				$executorFailRetryCount.val(executorFailRetryCount = 0);
            }

			// process schedule_conf
			var scheduleType = $form.find("select[name='scheduleType']").val();
			var scheduleConf;
			if (scheduleType == 'CRON') {
				scheduleConf = $form.find("input[name='cronGen_display']").val();
			} else if (scheduleType == 'FIX_RATE') {
				scheduleConf = $form.find("input[name='schedule_conf_FIX_RATE']").val();
			} else if (scheduleType == 'FIX_DELAY') {
				scheduleConf = $form.find("input[name='schedule_conf_FIX_DELAY']").val();
			}
			$form.find("input[name='scheduleConf']").val(scheduleConf);

			// post
			$.post(base_url + "/jobinfo/update", $form.serialize(), function (data, status) {
    			if (data.code == "200") {
					$('#updateModal').modal('hide');
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: I18n.system_update_suc ,
						icon: '1',
						end: function(layero, index){
							jobTable.fnDraw();
						}
					});
    			} else {
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: (data.msg || I18n.system_update_fail ),
						icon: '2'
					});
    			}
    		});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
        updateModalValidate.resetForm();
		var $form = $("#updateModal .form");
		$form[0].reset();
		$form.find(".form-group").removeClass("has-error");
	});

    /**
	 * find title by name, GlueType
     */
	function findGlueTypeTitle(glueType) {
		var glueTypeTitle;
        $("#addModal .form select[name=glueType] option").each(function () {
			var $me = $(this), name = $me.val(), title = $me.text();
            if (glueType == name) {
                glueTypeTitle = title;
                return false
            }
        });
        return glueTypeTitle;
    }

    // job_copy
	$jobList.on('click', '.job_copy', function () {

		var id = $(this).parents('ul').attr("_id");
		var row = tableData['key' + id], $form  = $('#addModal .form');

		// fill base
		$form.find('select[name=jobGroup]').val(row.jobGroup);
		$form.find("input[name=jobDesc]").val(row.jobDesc);
		$form.find("input[name=author]").val(row.author);
		$form.find("input[name=alarmEmail]").val(row.alarmEmail);

		// fill trigger
		$form.find('select[name=scheduleType]').val(row.scheduleType);
		$form.find("input[name=scheduleConf]").val(row.scheduleConf);
		if (row.scheduleType == 'CRON') {
			$form.find("input[name=schedule_conf_CRON]").val(row.scheduleConf);
		} else if (row.scheduleType == 'FIX_RATE') {
			$form.find("input[name=schedule_conf_FIX_RATE]").val(row.scheduleConf);
		} else if (row.scheduleType == 'FIX_DELAY') {
			$form.find("input[name=schedule_conf_FIX_DELAY]").val(row.scheduleConf);
		}

		// 》init scheduleType
		$form.find("select[name=scheduleType]").change();

		// fill job
		$form.find('select[name=glueType]').val(row.glueType);
		$form.find("input[name='executorHandler']").val(row.executorHandler);
		$form.find("textarea[name='executorParam']").val(row.executorParam);

		// 》init glueType
		$form.find("select[name=glueType]").change();

		// 》init-cronGen
		var $schedule_conf_CRON = $form.find("input[name='schedule_conf_CRON']");
		$schedule_conf_CRON.show().siblings().remove();
		$schedule_conf_CRON.cronGen({});

		// fill advanced
		$form.find('select[name=executorRouteStrategy]').val(row.executorRouteStrategy);
		$form.find("input[name=childJobId]").val(row.childJobId);
		$form.find('select[name=misfireStrategy]').val(row.misfireStrategy);
		$form.find('select[name=executorBlockStrategy]').val(row.executorBlockStrategy);
		$form.find("input[name=executorTimeout]").val(row.executorTimeout);
		$form.find("input[name=executorFailRetryCount]").val(row.executorFailRetryCount);

		// show
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});

});